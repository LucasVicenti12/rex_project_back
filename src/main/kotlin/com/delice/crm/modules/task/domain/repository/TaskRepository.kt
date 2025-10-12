package com.delice.crm.modules.task.domain.repository

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.task.domain.entities.Task
import com.delice.crm.modules.task.domain.entities.TaskHistory
import com.delice.crm.modules.task.domain.entities.TaskStatus
import java.util.UUID

interface TaskRepository {
    fun createTask(task: Task): Task?
    fun updateTask(task: Task): Task?
    fun getTaskByUUID(uuid: UUID): Task?
    fun deleteTask(taskUUID: UUID)
    fun getPaginatedTask(count: Int, page: Int, params: Map<String, Any?>): Pagination<Task>?

    fun changeTaskStatus(taskUUID: UUID, status: TaskStatus, user: User): Task?
    fun addTaskHistory(history: TaskHistory): Task?
}