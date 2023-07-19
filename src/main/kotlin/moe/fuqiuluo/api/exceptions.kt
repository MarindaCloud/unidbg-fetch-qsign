package moe.fuqiuluo.api

import java.lang.RuntimeException

object SessionNotFoundError: RuntimeException("Uin is not registered.")

object WrongKeyError: RuntimeException("Wrong API key.")

object MissingKeyError: RuntimeException("First use must be submitted with android_id and guid.")

object BlackListError: RuntimeException("Blacklist uin.")
