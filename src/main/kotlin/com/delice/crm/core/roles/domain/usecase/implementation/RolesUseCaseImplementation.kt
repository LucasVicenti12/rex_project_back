package com.delice.crm.core.roles.domain.usecase.implementation

import com.delice.crm.core.roles.domain.entities.Module
import com.delice.crm.core.roles.domain.entities.Role
import com.delice.crm.core.roles.domain.exceptions.*
import com.delice.crm.core.roles.domain.repository.RolesRepository
import com.delice.crm.core.roles.domain.usecase.RolesUseCase
import com.delice.crm.core.roles.domain.usecase.response.*
import com.delice.crm.core.utils.ordernation.OrderBy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class RolesUseCaseImplementation(
    private val rolesRepository: RolesRepository
) : RolesUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(RolesUseCaseImplementation::class.java)
    }

    override fun getRoles(): RoleListResponse = try {
        val roles = rolesRepository.getRoles()

        if (roles.isNullOrEmpty()) {
            RoleListResponse(roles = null, error = ROLES_IS_EMPTY)
        } else {
            RoleListResponse(roles = roles, error = null)
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_GET_ROLES", e)
        RoleListResponse(roles = null, error = ROLE_UNEXPECTED_ERROR)
    }

    override fun getModules(): ModuleListResponse = try {
        val modules = rolesRepository.getModules()

        if (modules.isNullOrEmpty()) {
            ModuleListResponse(modules = null, MODULES_IS_EMPTY)
        } else {
            ModuleListResponse(modules = modules, error = null)
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_GET_MODULES", e)
        ModuleListResponse(modules = null, error = ROLE_UNEXPECTED_ERROR)
    }

    override fun getModulesPagination(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): ModulePaginationResponse {
        return try {
            return ModulePaginationResponse(
                modules = rolesRepository.getModulesPagination(page, count, orderBy, params),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ROLES_MODULE_GET_MODULES", e)
            ModulePaginationResponse(error = ROLE_UNEXPECTED_ERROR)
        }
    }

    override fun getRolesPerUser(userUUID: UUID): RoleListResponse = try {
        val roles = rolesRepository.getRolesPerUser(userUUID)

        if (roles.isNullOrEmpty()) {
            RoleListResponse(roles = null, error = ROLES_IS_EMPTY)
        } else {
            RoleListResponse(roles = roles, error = null)
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_GET_ROLES_PER_USER", e)
        RoleListResponse(roles = null, error = ROLE_UNEXPECTED_ERROR)
    }

    override fun createRole(role: Role): RoleResponse = try {
        when {
            role.code.isNullOrEmpty() -> {
                RoleResponse(role = null, error = ROLE_CODE_IS_EMPTY)
            }

            role.label.isNullOrEmpty() -> {
                RoleResponse(role = null, error = ROLE_LABEL_IS_EMPTY)
            }

            role.moduleUUID == null || role.moduleUUID.toString().isEmpty() -> {
                RoleResponse(role = null, error = ROLE_MODULE_UUID_IS_EMPTY)
            }

            role.code.split(" ").size > 1 -> {
                RoleResponse(error = ROLE_WITH_INVALID_CODE)
            }

            rolesRepository.getRoleByCode(role.code) != null -> {
                RoleResponse(role = null, error = ROLE_ALREADY_EXISTS)
            }

            rolesRepository.getModuleByUUID(role.moduleUUID) == null -> {
                RoleResponse(role = null, error = MODULE_NOT_FOUND)
            }

            else -> {
                RoleResponse(role = rolesRepository.createRole(role), error = null)
            }
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_CREATE_ROLE", e)
        RoleResponse(role = null, error = ROLE_UNEXPECTED_ERROR)
    }

    override fun deleteRole(roleUUID: UUID): RoleDeleteResponse = try {
        val userWithRole = rolesRepository.verifyUserWithRole(roleUUID)

        if (userWithRole) {
            RoleDeleteResponse(message = "", error = ROLE_WITH_PERMISSION_ATTACH)
        } else {
            rolesRepository.deleteRole(roleUUID)

            RoleDeleteResponse(message = "Deleted with success", error = null)
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_DELETE_ROLE", e)
        RoleDeleteResponse(message = "", error = ROLE_UNEXPECTED_ERROR)
    }

    override fun createModule(module: Module): ModuleResponse = try {
        when {
            module.code.isNullOrEmpty() -> {
                ModuleResponse(module = null, error = MODULE_CODE_IS_EMPTY)
            }

            module.label.isNullOrEmpty() -> {
                ModuleResponse(module = null, error = MODULE_LABEL_IS_EMPTY)
            }

            (rolesRepository.getModuleByCode(module.code) != null && module.uuid === null) -> {
                ModuleResponse(module = null, error = MODULE_ALREADY_EXISTS)
            }

            module.uuid !== null -> {
                ModuleResponse(module = rolesRepository.editModule(module), error = null)
            }

            else -> {
                ModuleResponse(module = rolesRepository.createModule(module), error = null)
            }
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_CREATE_MODULE", e)
        ModuleResponse(module = null, error = ROLE_UNEXPECTED_ERROR)
    }

    override fun deleteModule(moduleUUID: UUID): ModuleDeleteResponse = try {
        val moduleWithRole = rolesRepository.verifyModuleWithRole(moduleUUID)

        if (moduleWithRole) {
            ModuleDeleteResponse(message = "", error = MODULE_WITH_ROLE_ATTACH)
        } else {
            rolesRepository.deleteModule(moduleUUID)

            ModuleDeleteResponse(message = "Deleted with success", error = null)
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_DELETE_MODULE", e)
        ModuleDeleteResponse(message = "", error = ROLE_UNEXPECTED_ERROR)
    }

    override fun attachRole(userUUID: UUID, roles: List<UUID>): RoleListResponse = try {
        when {
            userUUID.toString().isEmpty() -> {
                RoleListResponse(roles = null, error = USER_UUID_IS_EMPTY)
            }

            else -> {
                roles.forEach {
                    if (rolesRepository.getRoleByUUID(it) == null) {
                        return RoleListResponse(roles = null, error = ROLE_NOT_FOUND)
                    }
                }

                RoleListResponse(roles = rolesRepository.attachRole(userUUID, roles), error = null)
            }
        }
    } catch (e: Exception) {
        logger.error("ROLES_MODULE_ATTACH_ROLE_TO_USER", e)
        RoleListResponse(roles = null, error = ROLE_UNEXPECTED_ERROR)
    }

    override fun getModuleByUUID(uuid: UUID): ModuleResponse {
        val module = rolesRepository.getModuleByUUID(uuid)

        return if (module === null) {
            ModuleResponse(error = MODULE_NOT_FOUND)
        } else {
            ModuleResponse(module = module)
        }
    }

    override fun getModuleWithRolesByUUID(uuid: UUID): ModuleWithRolesResponse {
        val module = rolesRepository.getModuleByUUID(uuid)
        val roles = rolesRepository.getRolesByModuleUUID(uuid)

        return when {
            module === null -> {
                ModuleWithRolesResponse(error = MODULE_NOT_FOUND)
            }

            else -> {
                ModuleWithRolesResponse(
                    module = module,
                    roles = roles,
                )
            }
        }
    }

    override fun getAllRolesByModule(): RoleByModuleResponse = try {
        RoleByModuleResponse(
            modules = rolesRepository.getAllRolesByModule()
        )
    }catch (e: Exception){
        logger.error("ROLES_MODULE", e)
        RoleByModuleResponse(modules = null, error = ROLE_UNEXPECTED_ERROR)
    }
}