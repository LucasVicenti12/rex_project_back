package com.delice.crm.core.roles.domain.usecase

import com.delice.crm.core.roles.domain.entities.Module
import com.delice.crm.core.roles.domain.entities.Role
import com.delice.crm.core.roles.domain.usecase.response.*
import org.springframework.stereotype.Service
import java.util.*

@Service
interface RolesUseCase {
    fun getRoles(): RoleListResponse
    fun getModules(): ModuleListResponse
    fun getRolesPerUser(userUUID: UUID): RoleListResponse
    fun createRole(role: Role): RoleResponse
    fun deleteRole(roleUUID: UUID): RoleDeleteResponse
    fun createModule(module: Module): ModuleResponse
    fun deleteModule(moduleUUID: UUID): ModuleDeleteResponse
    fun attachRole(userUUID: UUID, roles: List<UUID>): RoleListResponse
}