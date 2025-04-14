package com.delice.crm.core.roles.domain.repository

import com.delice.crm.core.roles.domain.entities.DataModule
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
    fun getModuleByUUID(moduleUUID: UUID): Module?
    fun getModuleByCode(code: String): Module?
    fun getRolesPerUser(userUUID: UUID): List<Role>?
    fun getModuleRolesByUserUUID(userUUID: UUID): List<DataModule>?
    fun getOwnerRoles(): List<Role>?
    fun createRole(role: Role): Role?
    fun deleteRole(roleUUID: UUID)
    fun createModule(module: Module): Module?
    fun editModule(module: Module): Module?
    fun deleteModule(moduleUUID: UUID)
    fun attachRole(userUUID: UUID, roles: List<UUID>): List<Role>?
    fun verifyModuleWithRole(moduleUUID: UUID): Boolean
    fun verifyUserWithRole(roleUUID: UUID): Boolean
    fun getRolesByModuleUUID(uuid: UUID): List<Role>?
}