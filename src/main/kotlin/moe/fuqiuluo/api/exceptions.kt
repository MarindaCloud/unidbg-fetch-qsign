package moe.fuqiuluo.api

import java.lang.RuntimeException

object SessionNotFoundError: RuntimeException("Uin is not registered.")

object WrongKeyError: RuntimeException("Wrong API key.")
