package com.delice.crm.core.auth.domain.usecase.response

import com.delice.crm.core.auth.domain.exceptions.AuthException
import com.delice.crm.core.roles.domain.entities.Role
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.roles.domain.entities.Module as CRMModule;

data class AuthenticatedResponse (
    val user: User? = null,
    val modules: List<CRMModule>? = null,
    val roles: List<Role>? = null,
    val error: AuthException? = null
)