package com.delice.crm.modules.kanban.infra.database

import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import com.delice.crm.modules.kanban.domain.entities.CardBaseMetadata
import com.delice.crm.modules.kanban.domain.entities.ColumnRuleMetadata
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.stringLiteral

val mapper = jacksonObjectMapper()

object BoardDatabase : Table("kanban_board") {
    var uuid = uuid("uuid").uniqueIndex()
    var code = varchar("code", 20).uniqueIndex()
    var title = varchar("title", 90)
    var description = text("description").nullable()
    var status = integer("status")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_board_uuid")
}

data class BoardFilter(
    val parameters: Map<String, Any?>
): ExposedFilter<BoardDatabase> {
    override fun toFilter(table: BoardDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {

                val value = it.trim().lowercase()

                val generalFilter = Op.build {
                    (table.title like "%$value%") or
                            (table.code like "%$value%") or
                            (table.description like "%$value%")
                }

                op = op.and(generalFilter)
            }
        }

        parameters["status"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()

                val statusValue = if (value == "active") 0 else if (value == "inactive") 1 else null

                if (statusValue != null) {
                    op = op.and(table.status eq statusValue)
                } else {
                    op = op.and(Op.FALSE)
                }
            }
        }

        parameters["title"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.title like stringLiteral("%$it%"))
            }
        }

        parameters["description"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.description like stringLiteral("%$it%"))
            }
        }

        parameters["code"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.code like stringLiteral("%$it%"))
            }
        }

        return op
    }
}

object CardDatabase : Table("kanban_card") {
    var uuid = uuid("uuid").uniqueIndex()
    var boardUUID = uuid("board_uuid") references BoardDatabase.uuid
    var columnUUID = uuid("column_uuid") references ColumnDatabase.uuid
    var code = varchar("code", 20).uniqueIndex()
    var title = varchar("title", 90)
    var description = text("description").nullable()
    var metadata = json(
        name = "metadata",
        serialize = { mapper.writeValueAsString(it) },
        deserialize = { mapper.readValue<CardBaseMetadata>(it) },
    ).nullable()
    var tagUUID = uuid("tag_uuid").nullable()
    var status = integer("status")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_card_uuid")
}

object ColumnDatabase : Table("kanban_column") {
    var uuid = uuid("uuid").uniqueIndex()
    var boardUUID = uuid("board_uuid") references BoardDatabase.uuid
    var code = varchar("code", 20).uniqueIndex()
    var title = varchar("title", 90)
    var description = text("description").nullable()
    var type = varchar("type", 10)
    var status = integer("status")
    var index = integer("index")
    var isDefault = bool("is_default")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_column_uuid")
}

object ColumnAllowedDatabase : Table("kanban_allowed_column") {
    var uuid = uuid("uuid").uniqueIndex()
    var columnUUID = uuid("column_uuid") references ColumnDatabase.uuid
    var allowedColumnUUID = uuid("allowed_column_uuid") references ColumnDatabase.uuid
}

object TagDatabase : Table("kanban_tag") {
    var uuid = uuid("uuid").uniqueIndex()
    var boardUUID = uuid("board_uuid") references BoardDatabase.uuid
    var title = varchar("title", 30)
    var color = varchar("color", 9)
    var description = text("description")
    var status = integer("status")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_tag_uuid")
}

object ColumnRuleDatabase: Table("kanban_column_rule") {
    var uuid = uuid("uuid").uniqueIndex()
    val columnUUID = uuid("column_uuid") references ColumnDatabase.uuid
    val title = varchar("title", 90)
    val type = varchar("type", 30)
    val metadata = json(
        name = "metadata",
        serialize = { mapper.writeValueAsString(it) },
        deserialize = { mapper.readValue<ColumnRuleMetadata>(it) },
    ).nullable()
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")
}

data class KanbanOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<BoardDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) {
            return BoardDatabase.title to SortOrder.ASC
        }

        val orderByMap = mapOf(
            "uuid" to BoardDatabase.uuid,
            "title" to BoardDatabase.title,
            "code" to BoardDatabase.code,
            "description" to BoardDatabase.description,
            "status" to BoardDatabase.status,
            "created_at" to BoardDatabase.createdAt,
            "modified_at" to BoardDatabase.modifiedAt,
            "column_quantity" to ColumnDatabase.uuid.count(),
            "card_quantity" to CardDatabase.uuid.count()
        )

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val column = orderByMap[orderBy.orderBy]
        val sort = sortByMap[orderBy.sortBy]

        if (column == null || sort == null) {
            return BoardDatabase.title to SortOrder.ASC
        }

        return column to sort
    }
}