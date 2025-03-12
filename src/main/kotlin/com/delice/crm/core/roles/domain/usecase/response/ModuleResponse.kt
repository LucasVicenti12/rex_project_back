package com.delice.crm.core.roles.domain.usecase.response

import com.delice.crm.core.roles.domain.entities.Module
import com.delice.crm.core.roles.domain.exceptions.RoleException

data class ModuleResponse (
    val module: Module? = null,
    val error: RoleException? = null
)

data class ModuleListResponse (
    val modules: List<Module>? = listOf(),
    val error: RoleException? = null
)