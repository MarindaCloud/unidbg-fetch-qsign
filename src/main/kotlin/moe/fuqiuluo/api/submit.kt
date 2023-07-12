package moe.fuqiuluo.api

import com.tencent.mobileqq.channel.ChannelManager
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.fuqiuluo.ext.fetchGet
import moe.fuqiuluo.ext.hex2ByteArray


fun Routing.submit() {
    get("/submit") {
        val uin = fetchGet("uin")!!.toLong()
        val cmd = fetchGet("cmd")!!
        val callbackId = fetchGet("callback_id")!!.toLong()
        val buffer = fetchGet("buffer")!!.hex2ByteArray()

        val session = findSession(uin)

        session.withLock {
            ChannelManager.onNativeReceive(session.vm, cmd, buffer, callbackId)
            session.vm.global["HAS_SUBMIT"] = true
        }

        call.respond(APIResult(0, "submit success", ""))
    }
}