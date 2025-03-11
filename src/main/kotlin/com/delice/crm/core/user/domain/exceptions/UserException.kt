package com.delice.crm.core.user.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val USER_NOT_FOUND = UserException("USER_NOT_FOUND", "User not found")
val USER_UNEXPECTED = UserException("USER_UNEXPECTED", "An unexpected error occurred")

class UserException(code: String,message: String): DefaultError(code, message)