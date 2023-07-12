package moe.fuqiuluo.api

import java.lang.RuntimeException

object SessionNotFoundError: RuntimeException("Uin is not registered.")

object WrongKeyError: RuntimeException("Wrong API key.")

object MissingKeyError: RuntimeException("First use must be submitted with androidId and guid.")