@file:Suppress("UNCHECKED_CAST")
package moe.fuqiuluo.api

import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Serializable
import moe.fuqiuluo.ext.*
import moe.fuqiuluo.unidbg.session.SignStep


fun Routing.sign() {
    get("/sign") {
        val uin = fetchGet("uin")!!
        val qua = fetchGet("qua")!!
        val cmd = fetchGet("cmd")!!
        val seq = fetchGet("seq")!!.toInt()
        val buffer = fetchGet("buffer")!!.hex2ByteArray()
        val qimei36 = fetchGet("qua", def = "")!!

        requestSign(cmd, uin, qua, seq, buffer, qimei36)
    }

    post("/sign") {
        val param = call.receiveParameters()
        val uin = fetchPost(param, "uin")!!
        val qua = fetchPost(param, "qua")!!
        val cmd = fetchPost(param, "cmd")!!
        val seq = fetchPost(param, "seq")!!.toInt()
        val buffer = fetchPost(param, "buffer")!!.hex2ByteArray()
        val qimei36 = fetchPost(param, "qua", def = "")!!

        requestSign(cmd, uin, qua, seq, buffer, qimei36)
    }
}

@Serializable
private data class Sign(
    val token: String,
    val extra: String,
    val sign: String,
    val o3did: String,
    val requestCallback: List<SsoPacket>
)

private suspend fun PipelineContext<Unit, ApplicationCall>.requestSign(cmd: String, uin: String, qua: String, seq: Int, buffer: ByteArray, qimei36: String) {
    val session = findSession(uin.toLong())
    val vm = session.vm
    if (qimei36.isNotEmpty()) {
        vm.global["qimei36"] = qimei36
    }

    val list = arrayListOf<SsoPacket>()
    lateinit var o3did: String

    val sign = session.withLock {
        QQSecuritySign.getSign(vm, qua, cmd, buffer, seq, uin).value.also {
            o3did = vm.global["o3did"] as? String ?: ""
            val requiredPacket = vm.global["PACKET"] as ArrayList<SsoPacket>
            list.addAll(requiredPacket)
            requiredPacket.clear()
        }
    }

    call.respond(APIResult(0, "success", Sign(
        sign.token.toHexString(),
        sign.extra.toHexString(),
        sign.sign.toHexString(), o3did, list
    )))
}