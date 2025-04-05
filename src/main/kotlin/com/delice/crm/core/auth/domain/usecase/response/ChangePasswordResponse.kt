package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException

class ChangePasswordResponse (
    val ok: Boolean? = false,
    val error: AuthException? = null
)