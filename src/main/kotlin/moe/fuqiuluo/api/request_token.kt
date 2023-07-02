@file:Suppress("UNCHECKED_CAST")
package moe.fuqiuluo.api

import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import moe.fuqiuluo.ext.fetchGet

fun Routing.requestToken() {
    get("/request_token") {
        val uin = fetchGet("uin")!!.toLong()
        val session = findSession(uin)

        val vm = session.vm

        if ("HAS_SUBMIT" !in vm.global) {
            call.respond(APIResult(-1, "QSign not initialized, unable to request_ Token, please submit the initialization package first.", ""))
        } else {
            val list = arrayListOf<SsoPacket>()
            session.withLock {
                val lock = vm.global["mutex"] as Mutex
                lock.tryLock()
                QQSecuritySign.requestToken(vm)
                lock.withLock {
                    val requiredPacket = vm.global["PACKET"] as ArrayList<SsoPacket>
                    list.addAll(requiredPacket)
                    requiredPacket.clear()
                }
            }

            call.respond(APIResult(0, "submit success", list))
        }
    }
}