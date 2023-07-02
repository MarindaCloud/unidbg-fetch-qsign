package moe.fuqiuluo.api

import java.lang.RuntimeException

object UinWhiteListError: RuntimeException("Uin is not in the whitelist.")