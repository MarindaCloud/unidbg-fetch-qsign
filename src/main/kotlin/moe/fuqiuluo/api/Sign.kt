package moe.fuqiuluo.api

import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import moe.fuqiuluo.ext.*
import moe.fuqiuluo.unidbg.pool.work
import moe.fuqiuluo.unidbg.workerPool

@Serializable
private data class Sign(
    val token: String,
    val extra: String,
    val sign: String,
    val o3did: String
)

fun Routing.configSign() {
    get("/sign") {
        val uin = fetchGet("uin", err = "lack of uin") ?: return@get
        val qua = fetchGet("qua", err = "lack of qua") ?: return@get
        val cmd = fetchGet("cmd", err = "lack of cmd") ?: return@get
        val seq = (fetchGet("seq", err = "lack of seq") ?: return@get).toInt()
        val buffer = (fetchGet("buffer", err = "lack of buffer") ?: return@get).hex2ByteArray()
        val qimei36 = fetchGet("qimei36")

        this.requestSign(cmd, uin, qua, seq, buffer, qimei36 ?: "")
    }

    post("/sign") {
        val parameters = call.receiveParameters()

        val uin = fetchPost(parameters, "uin", err = "lack of uin") ?: return@post
        val qua = fetchPost(parameters, "qua", err = "lack of qua") ?: return@post
        val cmd = fetchPost(parameters, "cmd", err = "lack of cmd") ?: return@post
        val seq = (fetchPost(parameters, "seq", err = "lack of seq") ?: return@post).toInt()
        val buffer = (fetchPost(parameters, "buffer", err = "lack of buffer") ?: return@post).hex2ByteArray()
        val qimei36 = fetchPost(parameters, "qimei36")

        this.requestSign(cmd, uin, qua, seq, buffer, qimei36 ?: "")
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.requestSign(cmd: String, uin: String, qua: String, seq: Int, buffer: ByteArray, qimei36: String) {
    var o3did = ""
    val sign = workerPool.work {
        global["qimei36"] = qimei36
        FEKit.changeUin(this, uin)
        val sign = QQSecuritySign.getSign(this, qua, cmd, buffer, seq, uin).value
        o3did = global["o3did"] as? String ?: ""
        return@work sign
    }

    if (sign == null) {
        failure(-1, "The instance is occupied and there are no idle instances")
    } else {
        success(data = Sign(
            sign.token.toHexString(),
            sign.extra.toHexString(),
            sign.sign.toHexString(),
            o3did
        ))
    }
}