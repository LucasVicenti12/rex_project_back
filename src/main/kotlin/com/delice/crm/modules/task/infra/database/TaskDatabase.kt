package com.delice.crm.modules.task.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import com.delice.crm.modules.kanban.infra.database.mapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import java.time.format.DateTimeFormatter

object TaskDatabase : Table("task") {
    var uuid = uuid("uuid").uniqueIndex()
    var title = varchar("title", 90)
    var description = text("description")
    var responsible = uuid("responsible") references UserDatabase.uuid
    var status = integer("status")
    var priority = integer("priority")
    var dueDate = datetime("due_date")
    var createdBy = uuid("created_by") references UserDatabase.uuid
    var modifiedBy = uuid("modified_by") references UserDatabase.uuid
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_task")
}

object TaskHistoryDatabase : Table("task_history") {
    var uuid = uuid("uuid")
    var taskUUID = uuid("task_uuid") references TaskDatabase.uuid
    var description = text("description")
    var action = integer("action")
    val metadata = json(
        name = "metadata",
        serialize = { mapper.writeValueAsString(it) },
        deserialize = { mapper.readValue<String>(it) },
    ).nullable()
    var actionBy = uuid("action_by") references UserDatabase.uuid
    var actionAt = datetime("modified_at")
}

data class TaskFilter(
    val parameters: Map<String, Any?>
): ExposedFilter<TaskDatabase> {
    override fun toFilter(table: TaskDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()
                val numericValue = value.toIntOrNull()

                val decodedValue = java.net.URLDecoder.decode(it, "UTF-8")
                val searchTerms = decodedValue.trim().split(" ")

                val userUUIDsByName = UserDatabase
                    .select(UserDatabase.uuid)
                    .where {
                        searchTerms.map { term ->
                            (UserDatabase.name.lowerCase() like "%${term.lowercase()}%") or
                            (UserDatabase.surname.lowerCase() like "%${term.lowercase()}%") or
                            (UserDatabase.login.lowerCase() like "%${term.lowercase()}%")
                        }.reduce { acc, op -> acc and op }
                    }
                    .map { r -> r[UserDatabase.uuid] }

                val generalFilter = Op.build {
                    (numericValue?.let { nd -> table.status eq nd } ?: Op.FALSE) or
                            (numericValue?.let { nd -> table.priority eq nd } ?: Op.FALSE) or
                            (table.title like "%$value%") or
                            (table.description like "%$value%") or
                            (if (userUUIDsByName.isNotEmpty())
                                (table.responsible inList userUUIDsByName) or (table.createdBy inList userUUIDsByName)
                            else
                                Op.FALSE)
                }

                op = op.and(generalFilter)
            }
        }

        parameters["status"]?.let {
            it.toString().toIntOrNull()?.let { status ->
                op = op.and(table.status eq status)
            }
        }

        parameters["priority"]?.let {
            it.toString().toIntOrNull()?.let { priority ->
                op = op.and(table.priority eq priority)
            }
        }

        parameters["title"]?.let {
            if (it is String && it.isNotEmpty()) {
                op = op.and(table.title like "%$it%")
            }
        }

        parameters["description"]?.let {
            if (it is String && it.isNotEmpty()) {
                op = op.and(table.description like "%$it%")
            }
        }

        parameters["responsible"]?.let {
            if (it is String && it.isNotEmpty()) {
                val decodedValue = java.net.URLDecoder.decode(it, "UTF-8")
                val searchTerms = decodedValue.trim().split(" ") // Divide a string decodificada em palavras

                val subQuery = UserDatabase.select(UserDatabase.uuid).where {
                    searchTerms.map { term ->
                        (UserDatabase.name.lowerCase() like "%${term.lowercase()}%") or
                        (UserDatabase.surname.lowerCase() like "%${term.lowercase()}%")
                    }.reduce { acc, op -> acc and op }
                }

                op = op.and(table.responsible inSubQuery subQuery)
            }
        }

        parameters["created_by"]?.let {
            if (it is String && it.isNotEmpty()) {
                val decodedValue = java.net.URLDecoder.decode(it, "UTF-8")
                val searchTerms = decodedValue.trim().split(" ") // Divide a string decodificada em palavras

                val subQuery = UserDatabase.select(UserDatabase.uuid).where {
                    searchTerms.map { term ->
                        (UserDatabase.name.lowerCase() like "%${term.lowercase()}%") or
                        (UserDatabase.surname.lowerCase() like "%${term.lowercase()}%")
                    }.reduce { acc, op -> acc and op }
                }

                op = op.and(table.createdBy inSubQuery subQuery)
            }
        }

        parameters["due_date"]?.let {
            if (it is String && it.isNotBlank()) {
                try {
                    val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = java.time.LocalDate.parse(it, formater)

                    val startOfDay = date.atStartOfDay()
                    val endOfDay = date.atTime(23, 59, 59)

                    op = op.and (
                        op = table.dueDate.between(startOfDay, endOfDay)
                    )
                } catch (e: Exception) {
                    throw e
                }
            }
        }

        return op
    }
}

data class TaskOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<TaskDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) return TaskDatabase.title to SortOrder.ASC

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val sort = sortByMap[orderBy.sortBy] ?: SortOrder.ASC

        val column = when (orderBy.orderBy) {
            "title" -> TaskDatabase.title
            "responsible" -> {
                object : Expression<String>() {
                    override fun toQueryBuilder(queryBuilder: org.jetbrains.exposed.sql.QueryBuilder) {
                        queryBuilder.append("""
                            (SELECT CONCAT(u.name, ' ', u.surname) 
                             FROM users u 
                             WHERE u.uuid = task.responsible)
                        """.trimIndent())
                    }
                }
            }
            "created_by" -> {
                object : Expression<String>() {
                    override fun toQueryBuilder(queryBuilder: org.jetbrains.exposed.sql.QueryBuilder) {
                        queryBuilder.append("""
                            (SELECT CONCAT(u.name, ' ', u.surname) 
                             FROM users u 
                             WHERE u.uuid = task.created_by)
                        """.trimIndent())
                    }
                }
            }
            "status" -> TaskDatabase.status
            "priority" -> TaskDatabase.priority
            "due_date" -> TaskDatabase.dueDate

            else -> TaskDatabase.title
        }

        return column to sort
    }
}