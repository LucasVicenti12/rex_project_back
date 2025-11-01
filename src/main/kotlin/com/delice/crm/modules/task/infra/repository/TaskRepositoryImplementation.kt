package com.delice.crm.modules.task.infra.repository

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.convertDateTimeToDate
import com.delice.crm.core.utils.function.convertDateTimeToMonth
import com.delice.crm.core.utils.function.convertDateTimeToYear
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.task.domain.entities.*
import com.delice.crm.modules.task.domain.repository.TaskRepository
import com.delice.crm.modules.task.infra.database.TaskDatabase
import com.delice.crm.modules.task.infra.database.TaskFilter
import com.delice.crm.modules.task.infra.database.TaskHistoryDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Service
class TaskRepositoryImplementation(
    private val userRepository: UserRepository
) : TaskRepository {
    override fun createTask(task: Task): Task? = transaction {
        val newTaskUUID = UUID.randomUUID()

        TaskDatabase.insert {
            it[uuid] = newTaskUUID
            it[title] = task.title!!
            it[description] = task.description!!
            it[responsible] = task.responsible!!.uuid!!
            it[status] = task.status!!.code
            it[priority] = task.priority!!.code
            it[dueDate] = task.dueDate!!
            it[createdBy] = task.createdBy!!.uuid!!
            it[modifiedBy] = task.createdBy!!.uuid!!
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
        }

        val history = TaskHistory(
            taskUUID = newTaskUUID,
            action = TaskAction.CREATED,
            actionBy = task.createdBy,
        )

        return@transaction addTaskHistory(history)
    }

    override fun updateTask(task: Task): Task? = transaction {
        TaskDatabase.update({ TaskDatabase.uuid eq task.uuid!! }) {
            it[title] = task.title!!
            it[description] = task.description!!
            it[responsible] = task.responsible!!.uuid!!
            it[status] = task.status!!.code
            it[priority] = task.priority!!.code
            it[dueDate] = task.dueDate!!
            it[modifiedBy] = task.modifiedBy!!.uuid!!
            it[modifiedAt] = LocalDateTime.now()
        }

        val history = TaskHistory(
            taskUUID = task.uuid!!,
            action = TaskAction.UPDATED,
            actionBy = task.modifiedBy,
        )

        return@transaction addTaskHistory(history)
    }

    override fun getTaskByUUID(uuid: UUID): Task? = transaction {
        TaskDatabase.select(
            TaskDatabase.uuid,
            TaskDatabase.title,
            TaskDatabase.description,
            TaskDatabase.responsible,
            TaskDatabase.status,
            TaskDatabase.priority,
            TaskDatabase.dueDate,
            TaskDatabase.createdBy,
            TaskDatabase.createdAt,
            TaskDatabase.modifiedAt,
        ).where { TaskDatabase.uuid eq uuid }.map {
            resultRowToTask(it)
        }.firstOrNull()
    }

    override fun deleteTask(taskUUID: UUID) {
        transaction {
            TaskHistoryDatabase.deleteWhere {
                TaskHistoryDatabase.taskUUID eq taskUUID
            }

            TaskDatabase.deleteWhere { uuid eq taskUUID }
        }
    }

    override fun getPaginatedTask(count: Int, page: Int, params: Map<String, Any?>): Pagination<Task>? = transaction {
        val query = TaskDatabase
            .select(
                TaskDatabase.uuid,
                TaskDatabase.title,
                TaskDatabase.description,
                TaskDatabase.responsible,
                TaskDatabase.status,
                TaskDatabase.priority,
                TaskDatabase.dueDate,
                TaskDatabase.createdBy,
                TaskDatabase.createdAt,
                TaskDatabase.modifiedAt,
            )
            .where(TaskFilter(params).toFilter(TaskDatabase))

        val total = ceil(query.count().toDouble() / count).toInt()

        val items = query
            .limit(count)
            .offset((page * count).toLong())
            .map {
                resultRowToTask(it)
            }

        Pagination(
            items = items,
            page = page,
            total = total
        )
    }

    override fun changeTaskStatus(taskUUID: UUID, status: TaskStatus, user: User): Task? = transaction {
        TaskDatabase.update({ TaskDatabase.uuid eq taskUUID }) {
            it[TaskDatabase.status] = status.code
        }

        val action = when {
            status == TaskStatus.COMPLETED -> {
                TaskAction.FINISHED
            }

            else -> {
                TaskAction.UPDATED
            }
        }

        val history = TaskHistory(
            taskUUID = taskUUID,
            action = action,
            actionBy = user,
        )

        return@transaction addTaskHistory(history)
    }

    override fun addTaskHistory(history: TaskHistory): Task? = transaction {
        val newUUID = UUID.randomUUID()

        TaskHistoryDatabase.insert {
            it[uuid] = newUUID
            it[taskUUID] = history.taskUUID!!
            it[description] = history.description ?: ""
            it[action] = history.action!!.code
            it[actionBy] = history.actionBy!!.uuid!!
            it[actionAt] = LocalDateTime.now()
        }

        return@transaction getTaskByUUID(history.taskUUID!!)
    }

    override fun getTasksByMonth(month: Int, year: Int): List<TaskByDate>? = transaction {
        val taskMap = mutableMapOf<LocalDate, MutableList<Task>>()

        val monthTask = convertDateTimeToMonth(TaskDatabase.dueDate)
        val dateTask = convertDateTimeToDate(TaskDatabase.dueDate)
        val yearTask = convertDateTimeToYear(TaskDatabase.dueDate)

        TaskDatabase.select(
            TaskDatabase.uuid,
            monthTask,
            dateTask,
        ).where {
            monthTask eq month and (yearTask eq year)
        }.orderBy(TaskDatabase.dueDate, SortOrder.ASC).forEach {
            val date = it[dateTask]
            val task = getTaskByUUID(it[TaskDatabase.uuid]) ?: return@forEach

            val tasksForDay = taskMap.getOrPut(date) { mutableListOf() }
            tasksForDay.add(task)
        }

        val taskList = taskMap.map { (t, u) ->
            TaskByDate(
                tasks = u.toList(),
                day = t
            )
        }.sortedBy { it.day }

        return@transaction taskList
    }

    override fun getMyNextTask(userUUID: UUID): Task? = transaction {
        val taskUUID = TaskDatabase.select(TaskDatabase.uuid).where({
            TaskDatabase.responsible eq userUUID and (TaskDatabase.status neq TaskStatus.COMPLETED.code)
        }).limit(1)
            .map {
                it[TaskDatabase.uuid]
            }.firstOrNull() ?: return@transaction null

        return@transaction getTaskByUUID(taskUUID)
    }

    private fun getHistoryByTaskUUID(taskUUID: UUID): List<TaskHistory> = transaction {
        TaskHistoryDatabase.selectAll().where {
            TaskHistoryDatabase.taskUUID eq taskUUID
        }
            .orderBy(TaskHistoryDatabase.actionAt, SortOrder.DESC)
            .map {
                resultRowToTaskHistory(it)
            }
    }

    private fun resultRowToTask(it: ResultRow): Task = Task(
        uuid = it[TaskDatabase.uuid],
        title = it[TaskDatabase.title],
        description = it[TaskDatabase.description],
        responsible = userRepository.getUserByUUID(it[TaskDatabase.responsible]),
        status = enumFromTypeValue<TaskStatus, Int>(it[TaskDatabase.status]),
        priority = enumFromTypeValue<TaskPriority, Int>(it[TaskDatabase.priority]),
        history = getHistoryByTaskUUID(it[TaskDatabase.uuid]),
        dueDate = it[TaskDatabase.dueDate],
        createdBy = userRepository.getUserByUUID(it[TaskDatabase.createdBy]),
        createdAt = it[TaskDatabase.createdAt],
        modifiedAt = it[TaskDatabase.modifiedAt],
    )

    private fun resultRowToTaskHistory(it: ResultRow): TaskHistory = TaskHistory(
        uuid = it[TaskHistoryDatabase.uuid],
        taskUUID = it[TaskHistoryDatabase.taskUUID],
        description = it[TaskHistoryDatabase.description],
        action = enumFromTypeValue<TaskAction, Int>(it[TaskHistoryDatabase.action]),
        actionBy = userRepository.getUserByUUID(it[TaskHistoryDatabase.actionBy]),
        actionAt = it[TaskHistoryDatabase.actionAt],
    )
}