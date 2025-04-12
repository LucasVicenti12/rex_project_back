package com.delice.crm.core.roles.infra.repository

import com.delice.crm.core.roles.domain.entities.*
import com.delice.crm.core.roles.domain.repository.RolesRepository
import com.delice.crm.core.roles.infra.database.ModuleDatabase
import com.delice.crm.core.roles.infra.database.PermissionDatabase
import com.delice.crm.core.roles.infra.database.RoleDatabase
import com.delice.crm.core.user.domain.entities.UserType
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.enums.enumFromTypeValue
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.util.*

@Service
class RolesRepositoryImplementation : RolesRepository {
    override fun getRoles(): List<Role>? = transaction {
        RoleDatabase.selectAll().map {
            convertResultRowToRole(it)
        }
    }

    override fun getRoleByUUID(roleUUID: UUID): Role? = transaction {
        RoleDatabase.selectAll().where { RoleDatabase.uuid eq roleUUID }.map {
            convertResultRowToRole(it)
        }.firstOrNull()
    }

    override fun getRoleByCode(code: String): Role? = transaction {
        RoleDatabase.selectAll().where { RoleDatabase.code eq code }.map {
            convertResultRowToRole(it)
        }.firstOrNull()
    }

    override fun getModules(): List<Module>? = transaction {
        ModuleDatabase.selectAll().map {
            convertResultRowToModule(it)
        }
    }

    override fun getModuleByUUID(moduleUUID: UUID): Module? = transaction {
        ModuleDatabase.selectAll().where { ModuleDatabase.uuid eq moduleUUID }.map {
            convertResultRowToModule(it)
        }.firstOrNull()
    }

    override fun getModuleByCode(code: String): Module? = transaction {
        ModuleDatabase.selectAll().where { ModuleDatabase.code eq code }.map {
            convertResultRowToModule(it)
        }.firstOrNull()
    }

    override fun getRolesPerUser(userUUID: UUID): List<Role> = transaction {
        val userType: UserType = UserDatabase.select(UserDatabase.userType)
            .where { UserDatabase.uuid eq userUUID }
            .map { enumFromTypeValue<UserType, String>(it[UserDatabase.userType]) }
            .first()

        when (userType) {
            UserType.DEV -> getRoles()
            UserType.OWNER -> getOwnerRoles()
            else -> {
                (RoleDatabase innerJoin PermissionDatabase)
                    .selectAll()
                    .where { (PermissionDatabase.userUUID eq userUUID) and (RoleDatabase.uuid eq PermissionDatabase.roleUUID) }
                    .map { convertResultRowToRole(it) }
            }
        }!!
    }

    override fun getModuleRolesByUserUUID(userUUID: UUID): List<DataModule>? = transaction {
        val roles = getRolesPerUser(userUUID)

        if (roles.isEmpty()) {
            return@transaction emptyList()
        }

        val modules = roles.map { it.moduleUUID }.groupBy { it }.map { it.key }

        if (modules.isEmpty()) {
            return@transaction emptyList()
        }

        return@transaction modules.map {
            val module = getModuleByUUID(it!!)!!

            DataModule(
                code = module.code,
                path = module.path,
                roles = roles.map { role ->
                    DataRole(
                        code = role.code,
                        label = role.label
                    )
                }
            )
        }
    }

    override fun getOwnerRoles(): List<Role>? = transaction {
        RoleDatabase
            .selectAll()
            .where {
                RoleDatabase.roleType eq RoleType.USER.type
            }.map {
                convertResultRowToRole(it)
            }
    }

    override fun createRole(role: Role): Role? = transaction {
        RoleDatabase.insert {
            it[uuid] = UUID.randomUUID()
            it[code] = role.code!!
            it[label] = role.label!!
            it[roleType] = role.roleType!!.type
            it[moduleUUID] = role.moduleUUID!!
        }.resultedValues!!.map {
            convertResultRowToRole(it)
        }.firstOrNull()
    }

    override fun deleteRole(roleUUID: UUID) {
        transaction {
            RoleDatabase.deleteWhere { uuid eq roleUUID }
        }
    }

    override fun createModule(module: Module): Module? = transaction {
        ModuleDatabase.insert {
            it[uuid] = UUID.randomUUID()
            it[code] = module.code!!
            it[label] = module.label!!
            it[path] = module.path!!
        }.resultedValues!!.map {
            convertResultRowToModule(it)
        }.firstOrNull()
    }

    override fun deleteModule(moduleUUID: UUID) {
        transaction {
            ModuleDatabase.deleteWhere { uuid eq moduleUUID }
        }
    }

    override fun attachRole(userUUID: UUID, roles: List<UUID>): List<Role>? {
        transaction {
            PermissionDatabase.deleteWhere { PermissionDatabase.userUUID eq userUUID }

            roles.forEach { r ->
                PermissionDatabase.insert {
                    it[roleUUID] = r
                    it[PermissionDatabase.userUUID] = userUUID
                }
            }
        }

        return getRolesPerUser(userUUID)
    }

    override fun verifyModuleWithRole(moduleUUID: UUID): Boolean = transaction {
        RoleDatabase.selectAll().where { RoleDatabase.moduleUUID eq moduleUUID }.count() > 0
    }

    override fun verifyUserWithRole(roleUUID: UUID): Boolean = transaction {
        PermissionDatabase.selectAll().where { PermissionDatabase.roleUUID eq roleUUID }.count() > 0
    }

    private fun convertResultRowToRole(it: ResultRow): Role = Role(
        uuid = it[RoleDatabase.uuid],
        code = it[RoleDatabase.code],
        label = it[RoleDatabase.label],
        roleType = enumFromTypeValue<RoleType, String>(it[RoleDatabase.roleType]),
        moduleUUID = it[RoleDatabase.moduleUUID],
    )

    private fun convertResultRowToModule(it: ResultRow): Module = Module(
        uuid = it[ModuleDatabase.uuid],
        code = it[ModuleDatabase.code],
        label = it[ModuleDatabase.label],
        path = it[ModuleDatabase.path]
    )
}