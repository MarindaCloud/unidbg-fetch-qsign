package moe.fuqiuluo.unidbg.session

import CONFIG
import BASE_PATH
import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.fe.FEKit
import kotlinx.coroutines.sync.Mutex
import moe.fuqiuluo.comm.UinData
import moe.fuqiuluo.unidbg.QSecVM

class Session(uinData: UinData) {
    internal val vm: QSecVM =
        QSecVM(BASE_PATH, uinData, CONFIG.unidbg.dynarmic, CONFIG.unidbg.unicorn)
    internal val mutex = Mutex()

    init {
        vm.global["PACKET"] = arrayListOf<SsoPacket>()
        vm.global["mutex"] = Mutex(true)
        vm.global["qimei36"] = vm.uinData.qimei36.lowercase()
        vm.global["guid"] = vm.uinData.guid.lowercase()
        vm.init()
        FEKit.init(vm, uinData.uin.toString())
    }
}