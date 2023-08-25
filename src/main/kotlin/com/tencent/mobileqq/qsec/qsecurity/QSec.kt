package com.tencent.mobileqq.qsec.qsecurity

import com.github.unidbg.linux.android.dvm.DvmObject
import com.github.unidbg.linux.android.dvm.array.ByteArray
import moe.fuqiuluo.unidbg.QSecVM

object QSec {
    fun doSomething(vm: QSecVM, context: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/qsec/qsecurity/QSec", unique = true)
            .callJniMethodInt(vm.emulator, "doSomething(Landroid/content/Context;I)I", context, 1)
    }

    fun getEst(vm: QSecVM): ByteArray? {
        val context = vm.newInstance("android/content/Context", unique = true)
        return vm.newInstance("com/tencent/mobileqq/qsec/qsecest/QsecEst", unique = true)
            .callJniMethodObject(
                vm.emulator, "d(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)[B",
                context, vm.envData.guid, "")
    }
}