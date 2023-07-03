package moe.fuqiuluo.api

import kotlinx.coroutines.sync.withLock
import moe.fuqiuluo.unidbg.session.Session
import moe.fuqiuluo.unidbg.session.SessionManager

fun findSession(uin: Long): Session {
    return (SessionManager[uin] ?: throw SessionNotFoundError)
}

internal suspend inline fun <T> Session.withLock(block: () -> T): T {
    return mutex.withLock {
        block()
    }
}