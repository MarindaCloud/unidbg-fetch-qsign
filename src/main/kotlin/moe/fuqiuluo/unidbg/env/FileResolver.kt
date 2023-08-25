package moe.fuqiuluo.unidbg.env

import CONFIG
import com.github.unidbg.Emulator
import com.github.unidbg.file.FileResult
import com.github.unidbg.file.linux.AndroidFileIO
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.github.unidbg.linux.file.DirectoryFileIO
import com.github.unidbg.linux.file.SimpleFileIO
import com.github.unidbg.unix.UnixEmulator
import io.ktor.server.config.*
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.unidbg.QSecVM
import moe.fuqiuluo.unidbg.env.files.fetchCpuInfo
import moe.fuqiuluo.unidbg.env.files.fetchMemInfo
import moe.fuqiuluo.unidbg.env.files.fetchStat
import moe.fuqiuluo.unidbg.env.files.fetchStatus
import java.io.File
import java.util.UUID
import java.util.logging.Logger

class FileResolver(
    sdk: Int,
    val vm: QSecVM
): AndroidResolver(sdk) {
    private val tmpFilePath = vm.coreLibPath
    private val uuid = UUID.randomUUID()

    init {
        for (s in arrayOf("stdin", "stdout", "stderr")) {
            tmpFilePath.resolve(s).also {
                if (it.exists()) it.delete()
            }
        }
    }

    override fun resolve(emulator: Emulator<AndroidFileIO>, path: String, oflags: Int): FileResult<AndroidFileIO>? {
        val result = super.resolve(emulator, path, oflags)
        if (result == null || !result.isSuccess) {
            return this.resolve(emulator, path, oflags, result)
        }
        return result
    }

    private fun resolve(emulator: Emulator<AndroidFileIO>, path: String, oflags: Int, def: FileResult<AndroidFileIO>?): FileResult<AndroidFileIO>? {
        if (path == "stdin" || path == "stdout" || path == "stderr") {
            return FileResult.success(SimpleFileIO(oflags, tmpFilePath.resolve(path).also {
                if (!it.exists()) it.createNewFile()
            }, path))
        }

        if (path == "/data/data/com.tencent.tim/lib/libwtecdh.so") {
            return FileResult.failed(UnixEmulator.ENOENT)
        }


        if (path == "/proc/sys/kernel/random/boot_id") {
            return FileResult.success(ByteArrayFileIO(oflags, path, uuid.toString().toByteArray()))
        }
        if (path == "/proc/self/status") {
            return FileResult.success(ByteArrayFileIO(oflags, path, fetchStatus(emulator.pid).toByteArray()))
        }
        if (path == "/proc/stat") {
            return FileResult.success(ByteArrayFileIO(oflags, path, fetchStat()))
        }
        if (path == "/proc/meminfo") {
            return FileResult.success(ByteArrayFileIO(oflags, path, fetchMemInfo()))
        }
        if (path == "/proc/cpuinfo") {
            return FileResult.success(ByteArrayFileIO(oflags, path, fetchCpuInfo()))
        }
        if (path == "/dev/__properties__") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(true, "properties_serial"),
            ))
        }

        if ("/proc/self/maps" == path) {
            return FileResult.success(ByteArrayFileIO(oflags, path, byteArrayOf()))
        }

        if (path == "/system/lib") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(true, "libhwui.so"),
            ))
        }

        if (path == "/data/data/${vm.envData.packageName}") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(false, "files"),
                DirectoryFileIO.DirectoryEntry(false, "shared_prefs"),
                DirectoryFileIO.DirectoryEntry(false, "cache"),
                DirectoryFileIO.DirectoryEntry(false, "code_cache"),
            ))
        }

        if (path == "/dev/urandom" ||
            path == "/data/local/su" ||
            path == "/data/local/bin/su" ||
            path == "/data/local/xbin/su" ||
            path == "/sbin/su" ||
            path == "/su/bin/su" ||
            path == "/system/bin/su" ||
            path == "/system/bin/.ext/su" ||
            path == "/system/bin/failsafe/su" ||
            path == "/system/sd/xbin/su" ||
            path == "/system/usr/we-need-root/su" ||
            path == "/system/xbin/su" ||
            path == "/cache/su" ||
            path == "/data/su" ||
            path == "/dev/su" || path.contains("busybox") || path.contains("magisk")
            ) {
            return FileResult.failed(UnixEmulator.ENOENT)
        }

        if (path == "/sdcard/Android/") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(false, "data"),
                DirectoryFileIO.DirectoryEntry(false, "obb"),
            ))
        }

        if (path == "/system/lib64/libhoudini.so" || path == "/system/lib/libhoudini.so") {
            return FileResult.failed(UnixEmulator.ENOENT)
        }

        if (path == "/proc/self/cmdline"
            || path == "/proc/${emulator.pid}/cmdline"
            || path == "/proc/stat/cmdline" // an error case
        ) {
            if (vm.envData.packageName == "com.tencent.tim") {
                return FileResult.success(ByteArrayFileIO(oflags, path, vm.envData.packageName.toByteArray()))
            } else {
                return FileResult.success(ByteArrayFileIO(oflags, path, "${vm.envData.packageName}:MSF".toByteArray()))
            }
        }

        if (path == "/data/data") {
            return FileResult.failed(UnixEmulator.EACCES)
        }

        if (path.contains("star_est.xml")) {
            return FileResult.success(ByteArrayFileIO(oflags, path, """
            <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
            <map>
                <string name="id">NS23gm77vjYiyYK554L4aY0SYG5Xgjje</string>
            </map>
            """.trimIndent().toByteArray()))
        }

        if (path == "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq") {
            return FileResult.success(ByteArrayFileIO(oflags, path, "1804800".toByteArray()))
        }

        if (path == "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq") {
            return FileResult.success(ByteArrayFileIO(oflags, path, "300000".toByteArray()))
        }

        if (path == "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq") {
            return FileResult.success(ByteArrayFileIO(oflags, path, "1670400".toByteArray()))
        }

        if (path == "/sys/devices/soc0/serial_number") {
            return FileResult.success(ByteArrayFileIO(oflags, path, "0x0000043be8571339".toByteArray()))
        }

        if (path == "/proc") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(false, emulator.pid.toString()),
            ))
        }

        if (path == "/data/app/~~vbcRLwPxS0GyVfqT-nCYrQ==/${vm.envData.packageName}-xJKJPVp9lorkCgR_w5zhyA==/lib/arm64") {
            if (CONFIG.unidbg.debug)
                println("尝试获取library| 但是我不给")
            //return FileResult.success(DirectoryFileIO(oflags, path,
            //    DirectoryFileIO.DirectoryEntry(true, "libfekit.so"),
            //))
        }

        if(path == "/data/app/~~vbcRLwPxS0GyVfqT-nCYrQ==/${vm.envData.packageName}-xJKJPVp9lorkCgR_w5zhyA==/base.apk") {
            val f = tmpFilePath.resolve("QQ.apk")
            if (f.exists()) {
                return FileResult.success(SimpleFileIO(oflags, tmpFilePath.resolve("QQ.apk").also {
                    if (!it.exists()) it.createNewFile()
                }, path))
            } else {
                return FileResult.failed(UnixEmulator.ENOENT)
            }
        }

        if (path == "/data/app/~~vbcRLwPxS0GyVfqT-nCYrQ==/${vm.envData.packageName}-xJKJPVp9lorkCgR_w5zhyA==/lib/arm64/libfekit.so") {
            return FileResult.success(SimpleFileIO(oflags, tmpFilePath.resolve("libfekit.so"), path))
        }

        if (path == "/data/app/~~vbcRLwPxS0GyVfqT-nCYrQ==/${vm.envData.packageName}-xJKJPVp9lorkCgR_w5zhyA==/lib/arm64/libwtecdh.so") {
            tmpFilePath.resolve("libwtecdh.so").let {
                if (it.exists()) {
                    return FileResult.success(SimpleFileIO(oflags, it, path))
                }
            }
        }

        if (path == "/system/bin/sh" || path == "/system/bin/ls" || path == "/system/lib/libc.so") {
            return FileResult.success(ByteArrayFileIO(oflags, path, byteArrayOf(0x7f, 0x45, 0x4c, 0x46, 0x02, 0x01, 0x01, 0x00)))
        }

        if (path.startsWith("/data/user/")) {
            if (path != "/data/user/0" && path != "/data/user/999") {
                return FileResult.failed(UnixEmulator.ENOENT)
            } else {
                return FileResult.failed(UnixEmulator.EACCES)
            }
        }

        if (path.contains("system_android_l2") || path.contains("android_lq")) {
            val newPath = if (path.startsWith("C:")) path.substring(2) else path
            val file = tmpFilePath.resolve(".system_android_l2")
            if (!file.exists()) {
                file.writeBytes("619F9042CA821CF91DFAF172D464FFC7A6CB8E024CC053F7438429FA38E86854471D6B0A9DE4C39BF02DC18C0CC54A715C9210E8A32B284366849CBB7F88C634AA".hex2ByteArray())
            }
            return FileResult.success(SimpleFileIO(oflags, file, newPath))
        }

        Logger.getLogger("FileResolver").warning("Couldn't find file: $path")
        return def
    }
}