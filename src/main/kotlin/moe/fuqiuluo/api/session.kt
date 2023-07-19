package moe.fuqiuluo.api

import CONFIG
import kotlinx.coroutines.sync.withLock
import moe.fuqiuluo.unidbg.session.Session
import moe.fuqiuluo.unidbg.session.SessionManager

fun initSession(uin: Long): Session? {
    return SessionManager[uin] ?: if (!CONFIG.autoRegister) {
        throw SessionNotFoundError
    } else {
        null
    }
}

fun findSession(uin: Long): Session {
    return SessionManager[uin] ?: throw SessionNotFoundError
}

internal suspend inline fun <T> Session.withLock(block: () -> T): T {
    return mutex.withLock {
        block()
    }
}