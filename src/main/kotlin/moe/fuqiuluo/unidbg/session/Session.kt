package moe.fuqiuluo.unidbg.session

import CONFIG
import BASE_PATH
import kotlinx.coroutines.sync.Mutex
import moe.fuqiuluo.comm.UinData
import moe.fuqiuluo.unidbg.QSecVM

enum class SignStep {
    Step0, // init uin
    Step1, // establishShareKey
    Step2, // a2sec
    Step3, // wait to next refresh
}

class Session(uinData: UinData) {
    internal val vm: QSecVM =
        QSecVM(BASE_PATH, uinData, CONFIG.unidbg.dynarmic, CONFIG.unidbg.unicorn)
    internal var step: SignStep = SignStep.Step0
    internal val mutex = Mutex()

}