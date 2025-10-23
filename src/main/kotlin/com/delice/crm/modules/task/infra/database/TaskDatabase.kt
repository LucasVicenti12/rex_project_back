package com.delice.crm.modules.task.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.modules.kanban.infra.database.mapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

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
            if(it is String && it.isNotEmpty()) {
                val subQuery = UserDatabase.select(UserDatabase.uuid).where {
                    concat(
                        UserDatabase.login,
                        UserDatabase.name,
                        UserDatabase.surname,
                    ) eq it
                }

                op = op.and(table.responsible inSubQuery subQuery)
            }
        }

        parameters["createdBy"]?.let {
            if(it is String && it.isNotEmpty()) {
                val subQuery = UserDatabase.select(UserDatabase.uuid).where {
                    concat(
                        UserDatabase.login,
                        UserDatabase.name,
                        UserDatabase.surname,
                    ) eq it
                }

                op = op.and(table.createdBy inSubQuery subQuery)
            }
        }

        return op
    }
}