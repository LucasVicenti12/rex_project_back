package com.delice.crm.core.roles.domain.usecase.response

import com.delice.crm.core.roles.domain.entities.Module
import com.delice.crm.core.roles.domain.entities.Role
import com.delice.crm.core.roles.domain.exceptions.RoleException
import com.delice.crm.core.utils.pagination.Pagination

data class ModuleResponse(
    val module: Module? = null,
    val error: RoleException? = null
)

data class ModuleListResponse(
    val modules: List<Module>? = listOf(),
    val error: RoleException? = null
)

data class ModulePaginationResponse(
    val modules: Pagination<Module>? = null,
    val error: RoleException? = null
)

data class ModuleDeleteResponse(
    val message: String,
    val error: RoleException? = null
)

data class ModuleWithRolesResponse (
    val module: Module? = null,
    val roles: List<Role>? = listOf(),
    val error: RoleException? = null,
)