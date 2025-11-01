package com.delice.crm.modules.task.domain.usecase

import com.delice.crm.modules.task.domain.entities.Task
import com.delice.crm.modules.task.domain.entities.TaskHistory
import com.delice.crm.modules.task.domain.entities.TaskStatus
import com.delice.crm.modules.task.domain.usecase.response.MessageTaskResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskByDateResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskPaginatedResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskResponse
import java.util.UUID

interface TaskUseCase {
    fun createTask(task: Task): TaskResponse
    fun updateTask(task: Task): TaskResponse
    fun getTaskByUUID(uuid: UUID): TaskResponse
    fun deleteTask(taskUUID: UUID): MessageTaskResponse
    fun getPaginatedTask(count: Int, page: Int, params: Map<String, Any?>): TaskPaginatedResponse

    fun changeTaskStatus(taskUUID: UUID, status: TaskStatus): TaskResponse
    fun addTaskHistory(history: TaskHistory): TaskResponse

    fun getTasksByMonth(month: Int, year: Int): TaskByDateResponse

    fun getMyNextTask(): TaskResponse
}