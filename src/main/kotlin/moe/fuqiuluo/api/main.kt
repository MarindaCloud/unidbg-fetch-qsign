package moe.fuqiuluo.api

import BuildConfig
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class APIResult<T>(val code: Int,
                        val msg: String = "",
                        @Contextual
                        val data: T? = null)

fun Routing.index() {
    get("/") {
        @Serializable
        data class APIInfo (
            val version: String = BuildConfig.version
        )
        call.respond(APIResult(0, "IAA 云天明 章北海", APIInfo()))
    }
}
