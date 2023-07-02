package moe.fuqiuluo.api

import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.fe.FEKit
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import moe.fuqiuluo.ext.fetchGet
import moe.fuqiuluo.unidbg.QSecVM
import moe.fuqiuluo.unidbg.session.SignStep

fun Routing.queryStatus() {
    get("/query") {
        val uin = fetchGet("uin")!!.toLong()
        val session = findSession(uin)
        val global = session.vm.global
        if (session.step == SignStep.Step0) {
            val lock = Mutex(true).also {
                global["mutex"] = it
            }
            execStep0(session.vm, uin.toString())
            session.step = SignStep.Step1
            lock.withLock {
                call.respond(APIResult(
                    code = 0,
                    msg = "need to send packet and submit callback",
                    data = global["PACKET"] as SsoPacket
                ))
            }
        }
        call.respondText("AAA")
    }
}

private fun execStep0(vm: QSecVM, uin: String) {
    vm.global["qimei36"] = vm.uinData.qimei36.lowercase()
    vm.global["guid"] = vm.uinData.guid.lowercase()
    vm.init()
    FEKit.init(vm, uin)
}