package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException

data class ChangePasswordResponse(
    val message: String? = null,
    val error: AuthException? = null,
)