package com.delice.crm.core.roles.domain.repository

import com.delice.crm.core.roles.domain.entities.Role
import org.springframework.stereotype.Service
import java.util.UUID
import com.delice.crm.core.roles.domain.entities.Module

@Service
interface RolesRepository {
    fun getRoles(): List<Role>?
    fun getRoleByUUID(roleUUID: UUID): Role?
    fun getRoleByCode(code: String): Role?
    fun getModules(): List<Module>?
    fun getModuleByUUID(roleUUID: UUID): Module?
    fun getModuleByCode(code: String): Module?
    fun getRolesPerUser(userUUID: UUID): List<Role>?
    fun createRole(role: Role): Role?
    fun deleteRole(roleUUID: UUID)
    fun createModule(module: Module): Module?
    fun deleteModule(moduleUUID: UUID)
    fun attachRole(userUUID: UUID, roleUUID: UUID): List<Role>?
}