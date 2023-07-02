package moe.fuqiuluo.unidbg

import com.github.unidbg.linux.android.dvm.DvmObject
import com.tencent.mobileqq.qsec.qsecurity.DeepSleepDetector
import kotlinx.coroutines.DelicateCoroutinesApi
import moe.fuqiuluo.comm.UinData
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
    val uinData: UinData,
    dynarmic: Boolean,
    unicorn: Boolean
): Destroyable, AndroidVM("com.tencent.mobileqq", dynarmic, unicorn) {
    companion object {
        private val logger = LoggerFactory.getLogger(QSecVM::class.java)!!
    }

    private var destroy: Boolean = false
    private var isInit: Boolean = false
    internal val global = GlobalData()

    init {
        runCatching {
            val resolver = FileResolver(23, this@QSecVM)
            memory.setLibraryResolver(resolver)
            emulator.syscallHandler.addIOResolver(resolver)
            vm.setJni(QSecJni(uinData, this, global))
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun init() {
        if (isInit) return
        runCatching {
            loadLibrary(coreLibPath.resolve("libQSec.so"))
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