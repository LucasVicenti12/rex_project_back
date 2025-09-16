package com.delice.crm.core.roles.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

object ModuleDatabase: Table("modules"){
    var uuid = uuid("uuid").uniqueIndex()
    var code = varchar("code", 50).uniqueIndex()
    var label = varchar("label", 255)
    var path = varchar("path", 20)
}

data class ModuleFilter(
    val parameters: Map<String, Any?>
) : ExposedFilter<ModuleDatabase> {
    override fun toFilter(table: ModuleDatabase): org.jetbrains.exposed.sql.Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()

                val generalFilter = Op.build {
                    (table.code like "%$value%") or
                            (table.label like "%$value%") or
                            (table.path like "%$value%")
                }

                op = op.and(generalFilter)
            }
        }

        parameters["code"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.code like "%$it%")
            }
        }

        parameters["label"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.label like "%$it%")
            }
        }

        parameters["path"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.path like "%$it%")
            }
        }

        return op
    }
}

data class ModuleOrderBy (
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<ModuleDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) {
            return ModuleDatabase.label to SortOrder.ASC
        }

        val orderByMap = mapOf(
            "uuid" to ModuleDatabase.uuid,
            "code" to ModuleDatabase.code,
            "label" to ModuleDatabase.label,
            "path" to ModuleDatabase.path,
        )

        val sortyByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC
        )

        val column = orderByMap[orderBy.orderBy]
        val sort = sortyByMap[orderBy.sortBy]

        if (column == null || sort == null) {
            return ModuleDatabase.label to SortOrder.ASC
        }

        return column to sort
    }
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