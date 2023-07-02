package moe.fuqiuluo.unidbg.session

import CONFIG
import com.tencent.mobileqq.fe.FEKit
import java.util.concurrent.ConcurrentHashMap

object SessionManager {
    private val sessionMap = ConcurrentHashMap<Long, Session>()

    operator fun get(uin: Long): Session? {
        if (!sessionMap.contains(uin)) {
            val uinData = CONFIG.uinList
                .find { it.uin == uin } ?: return null
            sessionMap[uin] = Session(uinData)
        }
        return sessionMap[uin]
    }

}