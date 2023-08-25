package com.tencent.mobileqq.channel

import com.github.unidbg.linux.android.dvm.DvmObject
import moe.fuqiuluo.unidbg.QSecVM

object ChannelManager {
    fun setChannelProxy(vm: QSecVM, proxy: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "setChannelProxy(Lcom/tencent/mobileqq/channel/ChannelProxy;)V", proxy)
    }

    fun initReport(vm: QSecVM, qua: String, version: String, androidOs: String = "12", brand: String = "Redmi", model: String = "23013RK75C",
                   qimei36: String = vm.global["qimei36"] as? String ?: "", guid: String = vm.global["guid"] as? String ?: "") {
        runCatching {
            vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
                .callJniMethod(vm.emulator, "initReport(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                    qua, version, androidOs, brand + model, qimei36, guid
                )
        }.onFailure {
            vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
                .callJniMethod(vm.emulator, "initReport(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                    qua, version, androidOs, brand + model
                )
        }
    }

    fun onNativeReceive(vm: QSecVM, cmd: String, data: ByteArray, callbackId: Long) {
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "onNativeReceive(Ljava/lang/String;[BZJ)V",
                cmd, data, true, callbackId)
    }
}