package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException
import com.delice.crm.core.user.domain.entities.User

data class RegisterResponse(
    val user: User? = null,
    val error: AuthException? = null
)