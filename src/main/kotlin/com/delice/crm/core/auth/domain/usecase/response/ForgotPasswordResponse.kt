package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException

class ForgotPasswordResponse (
    val error: AuthException? = null
)