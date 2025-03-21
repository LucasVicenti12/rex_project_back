package com.delice.crm.core.auth.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val AUTH_UNEXPECTED = AuthException("AUTH_UNEXPECTED", "An unexpected error occurred")
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
val PASSWORDS_DON_T_MATCH = AuthException("PASSWORDS_DON_T_MATCH", "The passwords don't match")
val CURRENT_PASSWORD_MUST_BE_PROVIDED = AuthException("CURRENT_PASSWORD_MUST_BE_PROVIDED", "The current password must be provided")
val NEW_PASSWORD_MUST_BE_PROVIDED = AuthException("NEW_PASSWORD_MUST_BE_PROVIDED", "The new password must be provided")
val USER_UUID_IS_EMPTY = AuthException("USER_UUID_IS_EMPTY", "The user must be provided")

class AuthException(code: String, message: String) : DefaultError(code, message)