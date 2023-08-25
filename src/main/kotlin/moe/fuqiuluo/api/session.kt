package moe.fuqiuluo.api

import CONFIG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import moe.fuqiuluo.unidbg.session.Session
import moe.fuqiuluo.unidbg.session.SessionManager
import kotlin.concurrent.timer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

internal suspend inline fun <T> Session.withLock(action: () -> T): T {
    return mutex.withLockAndTimeout(5000, action)
}

@OptIn(ExperimentalContracts::class)
private suspend inline fun <T> Mutex.withLockAndTimeout(timeout: Long, action: () -> T): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    lock()
    val job = timer(initialDelay = timeout, period = timeout) {
        if (isLocked)
            unlock()
    }
    try {
        return action().also {
            job.cancel()
        }
    } finally {
        try {
            unlock()
        } catch (e: java.lang.Exception) {}
    }
}