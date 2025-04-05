package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException
import com.delice.crm.core.roles.domain.entities.DataModule
import com.delice.crm.core.user.domain.entities.User

data class AuthenticatedResponse (
    val user: User? = null,
    val modules: List<DataModule>? = emptyList(),
    val error: AuthException? = null
)