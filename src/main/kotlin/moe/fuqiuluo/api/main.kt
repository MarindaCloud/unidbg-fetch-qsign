package moe.fuqiuluo.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.index() {
    get("/") {
        call.respondTextWriter {
            write("IAA 云天明\n")
            write("Time: ")
            write(System.currentTimeMillis().toString())
        }
    }
}
