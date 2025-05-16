package com.delice.crm.core.user.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val USER_NOT_FOUND = UserException("USER_NOT_FOUND", "User not found")
val USER_UNEXPECTED = UserException("USER_UNEXPECTED", "An unexpected error occurred")

val NAME_MUST_BE_PROVIDED = UserException("NAME_MUST_BE_PROVIDED", "Name or surname must provided")
val EMAIL_MUST_BE_PROVIDED = UserException("EMAIL_MUST_BE_PROVIDED", "Email must provided")
val CITY_MUST_BE_PROVIDED = UserException("CITY_MUST_BE_PROVIDED", "The city of user must be provided")
val STATE_MUST_BE_PROVIDED = UserException("STATE_MUST_BE_PROVIDED", "The state of user must be provided")
val ZIP_CODE_MUST_BE_PROVIDED = UserException("ZIP_CODE_MUST_BE_PROVIDED", "The zip code of user must be provided")
val ADDRESS_MUST_BE_PROVIDER = UserException("ADDRESS_MUST_BE_PROVIDER", "The address of user must be provided")

class UserException(code: String,message: String): DefaultError(code, message)