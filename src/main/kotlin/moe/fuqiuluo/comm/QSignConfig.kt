@file:OptIn(ExperimentalSerializationApi::class)
package moe.fuqiuluo.comm

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Server(
    var host: String,
    var port: Int
)

@Serializable
data class UinData(
    var uin: Long,
    @JsonNames("androidId", "android_id", "imei")
    var androidId: String
)

@Serializable
data class Protocol(
    @JsonNames("sub_app_id")
    var subAppId: Long,
    var qua: String,
    var version: String,
    var code: String
)

@Serializable
data class UnidbgConfig(
    var dynarmic: Boolean,
    var unicorn: Boolean,
    var debug: Boolean,
)

@Serializable
data class QSignConfig(
    var server: Server,
    @JsonNames("uinList", "uin_list")
    var uinList: ArrayList<UinData>,
    @JsonNames("reloadInterval", "reload_interval")
    var reloadInterval: Int,
    var protocol: Protocol,
    var unidbg: UnidbgConfig,
)

fun QSignConfig.checkIllegal() {
    require(server.port in 1 .. 65535) { "Port is out of range." }
    require(reloadInterval in 20 .. 50) { "ReloadInterval is out of range." }
}