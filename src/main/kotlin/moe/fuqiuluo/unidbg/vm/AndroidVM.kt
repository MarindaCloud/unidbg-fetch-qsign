package moe.fuqiuluo.unidbg.vm

import CONFIG
import com.github.unidbg.arm.backend.DynarmicFactory
import com.github.unidbg.arm.backend.Unicorn2Factory
import com.github.unidbg.linux.android.AndroidEmulatorBuilder
import com.github.unidbg.linux.android.LogCatLevel
import com.github.unidbg.linux.android.dvm.DalvikModule
import com.github.unidbg.linux.android.dvm.DalvikVM64
import com.github.unidbg.linux.android.dvm.DvmClass
import com.github.unidbg.virtualmodule.android.AndroidModule
import org.apache.commons.logging.LogFactory
import java.io.Closeable
import java.io.File

open class AndroidVM(packageName: String, dynarmic: Boolean, unicorn: Boolean): Closeable {
    internal val emulator = AndroidEmulatorBuilder
        .for64Bit()
        .setProcessName(packageName)
        .apply {
            if (dynarmic) addBackendFactory(DynarmicFactory(true))
            if (unicorn) addBackendFactory(Unicorn2Factory(true))
            //if (unicorn) addBackendFactory(KvmFactory(true))
        }
        .build()!!
    protected val memory = emulator.memory!!
    internal val vm = emulator.createDalvikVM()!!

    init {
        if (CONFIG.unidbg.debug) {
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog")
            System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug")
            LogFactory.getFactory()
                .attributeNames.forEach { println(it) }
        }
        vm.setVerbose(CONFIG.unidbg.debug)
        val syscall = emulator.syscallHandler
        syscall.isVerbose = CONFIG.unidbg.debug
        syscall.setEnableThreadDispatcher(true)
        AndroidModule(emulator, vm).register(memory)
    }

    fun loadLibrary(soFile: File): DalvikModule {
        val dm = vm.loadLibrary(soFile, false)
        dm.callJNI_OnLoad(emulator)
        return dm
    }

    fun findClass(name: String, vararg interfaces: DvmClass): DvmClass {
        return vm.resolveClass(name, *interfaces)
    }

    override fun close() {
        this.emulator.close()
    }
}