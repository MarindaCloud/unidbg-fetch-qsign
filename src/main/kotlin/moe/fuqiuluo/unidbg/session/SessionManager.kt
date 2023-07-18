package moe.fuqiuluo.unidbg.session

import CONFIG
import moe.fuqiuluo.api.BlackListError
import moe.fuqiuluo.comm.EnvData
import java.util.concurrent.ConcurrentHashMap

object SessionManager {
    private val sessionMap = ConcurrentHashMap<Long, Session>()

    operator fun get(uin: Long): Session? {
        return sessionMap[uin]
    }

    operator fun contains(uin: Long) = sessionMap.containsKey(uin)

    fun register(envData: EnvData) {
        if (CONFIG.blackList?.contains(envData.uin) == true) {
            throw BlackListError
        }
        if (envData.uin in this) {
            close(envData.uin)
        }
        sessionMap[envData.uin] = Session(envData)
    }

    fun close(uin: Long) {
        sessionMap[uin]?.vm?.destroy()
        sessionMap.remove(uin)
    }
}