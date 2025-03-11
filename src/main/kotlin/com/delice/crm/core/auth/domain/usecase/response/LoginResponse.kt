package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException

data class LoginResponse (
    val token: String? = null,
    val error: AuthException? = null,
)