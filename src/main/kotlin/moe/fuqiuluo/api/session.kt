package moe.fuqiuluo.api

import moe.fuqiuluo.unidbg.session.Session
import moe.fuqiuluo.unidbg.session.SessionManager

fun findSession(uin: Long): Session {
    return SessionManager[uin] ?: throw UinWhiteListError
}

