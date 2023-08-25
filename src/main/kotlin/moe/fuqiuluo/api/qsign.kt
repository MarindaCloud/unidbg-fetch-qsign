@file:Suppress("UNCHECKED_CAST")

package moe.fuqiuluo.api

import CONFIG
import com.tencent.mobileqq.channel.SsoPacket
import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import moe.fuqiuluo.comm.EnvData
import moe.fuqiuluo.ext.*
import moe.fuqiuluo.unidbg.session.SessionManager

fun Routing.sign() {
    get("/sign") {
        val uin = fetchGet("uin")!!
        val qua = fetchGet("qua", CONFIG.protocol.qua)!!
        val cmd = fetchGet("cmd")!!
        val seq = fetchGet("seq")!!.toInt()
        val buffer = fetchGet("buffer")!!.hex2ByteArray()
        val qimei36 = fetchGet("qimei36", def = "")!!

        val androidId = fetchGet("android_id", def = "")!!
        val guid = fetchGet("guid", def = "")!!

        requestSign(cmd, uin, qua, seq, buffer, qimei36, androidId, guid)
    }

    post("/sign") {
        val param = call.receiveParameters()
        val uin = fetchPost(param, "uin")!!
        val qua = fetchPost(param, "qua", CONFIG.protocol.qua)!!
        val cmd = fetchPost(param, "cmd")!!
        val seq = fetchPost(param, "seq")!!.toInt()
        val buffer = fetchPost(param, "buffer")!!.hex2ByteArray()
        val qimei36 = fetchPost(param, "qimei36", def = "")!!

        val androidId = param["android_id"] ?: ""
        val guid = param["guid"] ?: ""

        requestSign(cmd, uin, qua, seq, buffer, qimei36, androidId, guid)
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

private suspend fun PipelineContext<Unit, ApplicationCall>.requestSign(
    cmd: String,
    uin: String,
    qua: String,
    seq: Int,
    buffer: ByteArray,
    qimei36: String,
    androidId: String,
    guid: String
) {
    val session = initSession(uin.toLong()) ?: run {
        if (androidId.isNullOrEmpty() || guid.isNullOrEmpty()) {
            throw MissingKeyError
        }
        SessionManager.register(
            EnvData(
                uin.toLong(),
                androidId,
                guid.lowercase(),
                qimei36,
                qua,
                CONFIG.protocol.version,
                CONFIG.protocol.code
            )
        )
        findSession(uin.toLong())
    }
    val vm = session.vm
    if (qimei36.isNotEmpty()) {
        vm.global["qimei36"] = qimei36
    }

    val list = arrayListOf<SsoPacket>()
    lateinit var o3did: String

    val sign = session.withLock {
        vm.global["est_data"] = QSec.getEst(vm)
        QQSecuritySign.getSign(vm, qua, cmd, buffer, seq, uin).value.also {
            o3did = vm.global["o3did"] as? String ?: ""
            val requiredPacket = vm.global["PACKET"] as ArrayList<SsoPacket>
            list.addAll(requiredPacket)
            requiredPacket.clear()
        }
    }

    call.respond(
        APIResult(
            0, "success", Sign(
                sign.token.toHexString(),
                sign.extra.toHexString(),
                sign.sign.toHexString(), o3did, list
            )
        )
    )
}