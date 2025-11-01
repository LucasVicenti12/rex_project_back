package com.delice.crm.modules.task.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.task.domain.entities.Task
import com.delice.crm.modules.task.domain.entities.TaskByDate
import com.delice.crm.modules.task.domain.exceptions.TaskException

data class TaskResponse(
    val task: Task? = null,
    val error: TaskException? = null
)

data class TaskPaginatedResponse(
    val tasks: Pagination<Task>? = null,
    val error: TaskException? = null,
)

data class MessageTaskResponse(
    val message: String? = null,
    val error: TaskException? = null
)

data class TaskByDateResponse(
    val tasks: List<TaskByDate>? = null,
    val error: TaskException? = null
)