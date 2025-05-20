package com.delice.crm.core.auth.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val AUTH_UNEXPECTED = AuthException("AUTH_UNEXPECTED", "An unexpected error occurred")
val INVALID_CREDENTIALS = AuthException("INVALID_CREDENTIALS", "Invalid login or password")
val LOGIN_MUST_BE_PROVIDED = AuthException("LOGIN_MUST_BE_PROVIDED", "The login must be provided")
val PASSWORD_MUST_BE_PROVIDED = AuthException("PASSWORD_MUST_BE_PROVIDED", "The password must be provided")
val NAME_MUST_BE_PROVIDED = AuthException("NAME_MUST_BE_PROVIDED", "The name or surname must be provided")
val DOCUMENT_MUST_BE_PROVIDED = AuthException("DOCUMENT_MUST_BE_PROVIDED", "The document must be provided")
val DOCUMENT_ALREADY_REGISTERED = AuthException("DOCUMENT_ALREADY_REGISTERED", "This document already registered")
val LOGIN_ALREADY_REGISTERED = AuthException("LOGIN_ALREADY_REGISTERED", "This login already registered")
val DATE_OF_BIRTH_MUST_BE_PROVIDED = AuthException("DATE_OF_BIRTH_MUST_BE_PROVIDED", "The date of birth must be provided")
val DATE_OF_BIRTH_INVALID = AuthException("DATE_OF_BIRTH_INVALID", "The date of birth is invalid")
val CITY_MUST_BE_PROVIDED = AuthException("CITY_MUST_BE_PROVIDED", "The city of user must be provided")
val STATE_MUST_BE_PROVIDED = AuthException("STATE_MUST_BE_PROVIDED", "The state of user must be provided")
val ZIP_CODE_MUST_BE_PROVIDED = AuthException("ZIP_CODE_MUST_BE_PROVIDED", "The zip code of user must be provided")
val ADDRESS_MUST_BE_PROVIDED = AuthException("ADDRESS_MUST_BE_PROVIDED", "The address of user must be provided")
val AUTH_USER_NOT_FOUND = AuthException("AUTH_USER_NOT_FOUND", "User not found")
val PASSWORDS_DONT_MATCH = AuthException("PASSWORDS_DONT_MATCH", "The passwords don't match")
val CONFIRM_PASSWORD_MUST_BE_PROVIDED = AuthException("CONFIRM_PASSWORD_MUST_BE_PROVIDED", "The confirm password must be provided")
val TOKEN_MUST_BE_PROVIDED = AuthException("TOKEN_MUST_BE_PROVIDED", "The token must be provided")
val INVALID_TOKEN = AuthException("INVALID_TOKEN", "The token is invalid")

class AuthException(code: String, message: String) : DefaultError(code, message)