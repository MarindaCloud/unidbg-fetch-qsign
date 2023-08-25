package moe.fuqiuluo.unidbg

import com.github.unidbg.linux.android.dvm.DvmObject
import com.tencent.mobileqq.qsec.qsecurity.DeepSleepDetector
import moe.fuqiuluo.comm.EnvData
import moe.fuqiuluo.unidbg.env.FileResolver
import moe.fuqiuluo.unidbg.env.QSecJni
import moe.fuqiuluo.unidbg.vm.AndroidVM
import moe.fuqiuluo.unidbg.vm.GlobalData
import org.slf4j.LoggerFactory
import java.io.File
import javax.security.auth.Destroyable
import kotlin.system.exitProcess

class QSecVM(
    val coreLibPath: File,
    val envData: EnvData,
    dynarmic: Boolean,
    unicorn: Boolean
): Destroyable, AndroidVM(envData.packageName, dynarmic, unicorn) {
    private var destroy: Boolean = false
    private var isInit: Boolean = false
    internal val global = GlobalData()

    init {
        runCatching {
            val resolver = FileResolver(23, this@QSecVM)
            memory.setLibraryResolver(resolver)
            emulator.syscallHandler.addIOResolver(resolver)
            vm.setJni(QSecJni(envData, this, global))

            if (envData.packageName == "com.tencent.mobileqq") {
                println("QSign-Unidbg 白名单模式")
                vm.setWhiteMode(true)
                arrayOf(
                    "android/os/Build\$VERSION",
                    "android/content/pm/ApplicationInfo",
                    "com/tencent/mobileqq/fe/IFEKitLog",
                    "com/tencent/mobileqq/channel/ChannelProxy",
                    "com/tencent/mobileqq/qsec/qsecurity/QSec",
                    "com/tencent/mobileqq/qsec/qsecurity/QSecConfig",
                    "com/tencent/mobileqq/sign/QQSecuritySign\$SignResult",
                    "java/lang/String",
                    "com/tencent/mobileqq/qsec/qsecest/QsecEst",
                    "com/tencent/qqprotect/qsec/QSecFramework",
                    "com/tencent/mobileqq/dt/app/Dtc",
                    "android/provider/Settings\$System",
                    "com/tencent/mobileqq/fe/utils/DeepSleepDetector",
                    "com/tencent/mobileqq/dt/model/FEBound",
                    "java/lang/ClassLoader",
                    "java/lang/Thread",
                    "android/content/Context",
                    "android/content/ContentResolver",
                    "java/io/File",
                    "java/lang/Integer",
                    "java/lang/Object",
                    "com/tencent/mobileqq/sign/QQSecuritySign",
                    "com/tencent/mobileqq/channel/ChannelManager",
                    "com/tencent/mobileqq/dt/Dtn",
                    "com/tencent/mobileqq/qsec/qsecdandelionsdk/Dandelion",
                    "com/tencent/mobileqq/qsec/qsecprotocol/ByteData",
                    "com/tencent/mobileqq/qsec/qseccodec/SecCipher",
                ).forEach {
                    vm.addFilterClass(it)
                }
            } else {
                vm.addFilterClass("com/tencent/mobileqq/dt/Dc")
                vm.addFilterClass("com/tencent/mobileqq/dt/Dte")
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun init() {
        if (isInit) return
        runCatching {
            coreLibPath.resolve("libpoxy.so").let {
                if (it.exists()) {
                    loadLibrary(it)
                }
            }
            loadLibrary(coreLibPath.resolve("libfekit.so"))
            global["DeepSleepDetector"] = DeepSleepDetector()
            this.isInit = true
        }.onFailure {
            it.printStackTrace()
            exitProcess(1)
        }
    }

    fun newInstance(name: String, value: Any? = null, unique: Boolean = false): DvmObject<*> {
        if (unique && name in global) {
            return global[name] as DvmObject<*>
        }
        val obj = findClass(name).newObject(value)
        if (unique) {
            global[name] = obj
        }
        return obj
    }

    override fun isDestroyed(): Boolean = destroy

    override fun destroy() {
        if (isDestroyed) return
        this.destroy = true
        this.close()
    }
}
