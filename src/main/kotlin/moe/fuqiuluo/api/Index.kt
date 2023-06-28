package moe.fuqiuluo.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import moe.fuqiuluo.ext.success

@Serializable
data class APIResult<T>(val code: Int,
                     val msg: String = "",
                     @Contextual
                     val data: T? = null)

fun Routing.configIndex() {
    get("/") {
        success(data = "unidbg-fetch-qsign: ${System.currentTimeMillis()}")
    }
}