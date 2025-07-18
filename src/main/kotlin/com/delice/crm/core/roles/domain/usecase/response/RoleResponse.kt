package com.delice.crm.core.roles.domain.usecase.response

import com.delice.crm.core.roles.domain.entities.DataModule
import com.delice.crm.core.roles.domain.entities.Role
import com.delice.crm.core.roles.domain.exceptions.RoleException

data class RoleResponse(
    val role: Role? = null,
    val error: RoleException? = null
)

data class RoleListResponse(
    val roles: List<Role>? = listOf(),
    val error: RoleException? = null
)

data class RoleByModuleResponse (
    val modules: List<DataModule>? = listOf(),
    val error: RoleException? = null
)

data class RoleDeleteResponse(
    val message: String,
    val error: RoleException? = null
)