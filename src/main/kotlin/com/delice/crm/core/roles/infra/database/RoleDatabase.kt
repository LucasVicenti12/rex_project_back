package com.delice.crm.core.roles.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import org.jetbrains.exposed.sql.Table

object ModuleDatabase: Table("modules"){
    var uuid = uuid("uuid").uniqueIndex()
    var code = varchar("code", 50).uniqueIndex()
    var label = varchar("label", 255)
}

object RoleDatabase: Table("roles") {
    var uuid = uuid("uuid").uniqueIndex()
    var code = varchar("code", 50).uniqueIndex()
    var label = varchar("label", 255)
    var roleType = varchar("role_type", 10)
    var moduleUUID = uuid("module_uuid").references(ModuleDatabase.uuid)
}

object PermissionDatabase: Table("permissions") {
    var userUUID = uuid("user_uuid").references(UserDatabase.uuid)
    var roleUUID = uuid("role_uuid").references(RoleDatabase.uuid)
}