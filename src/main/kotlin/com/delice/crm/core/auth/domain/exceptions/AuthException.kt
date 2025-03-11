package com.delice.crm.core.auth.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val AUTH_UNEXPECTED = AuthException("AUTH_UNEXPECTED", "An unexpected error occurred")
val LOGIN_MUST_BE_PROVIDED = AuthException("LOGIN_MUST_BE_PROVIDED", "The login must be provided")
val PASSWORD_MUST_BE_PROVIDED = AuthException("PASSWORD_MUST_BE_PROVIDED", "The password must be provided")
val NAME_MUST_BE_PROVIDED = AuthException("NAME_MUST_BE_PROVIDED", "The name or surname must be provided")
val DOCUMENT_MUST_BE_PROVIDED = AuthException("DOCUMENT_MUST_BE_PROVIDED", "The document must be provided")
val DOCUMENT_ALREADY_REGISTERED = AuthException("DOCUMENT_ALREADY_REGISTERED", "This document already registered")
val LOGIN_ALREADY_REGISTERED = AuthException("LOGIN_ALREADY_REGISTERED", "This login already registered")

class AuthException(code: String, message: String) : DefaultError(code, message)