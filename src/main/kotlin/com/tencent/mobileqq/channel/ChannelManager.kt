package com.tencent.mobileqq.channel

import com.github.unidbg.linux.android.dvm.DvmObject
import moe.fuqiuluo.unidbg.QSecVM

object ChannelManager {
    fun setChannelProxy(vm: QSecVM, proxy: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "setChannelProxy(Lcom/tencent/mobileqq/channel/ChannelProxy;)V", proxy)
    }

    fun initReport(vm: QSecVM, qua: String, version: String, androidOs: String = "12", brand: String = "Redmi", model: String = "23013RK75C",
                   qimei36: String = "", guid: String = "") {
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "initReport(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                qua, version, androidOs, brand + model, qimei36, guid
            )
    }

    fun onNativeReceive(vm: QSecVM, cmd: String, data: ByteArray, callbackId: Long) {
        while ("onNativeReceive" in vm.global) { }
        vm.global["onNativeReceive"] = true
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "onNativeReceive(Ljava/lang/String;[BZJ)V",
                cmd, data, true, callbackId)
        vm.global.remove("onNativeReceive")
    }
}