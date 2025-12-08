package com.delice.crm.modules.task.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val TASK_UNEXPECTED_ERROR = TaskException("TASK_UNEXPECTED_ERROR", "An unexpected error has occurred")
val TASK_TITLE_IS_EMPTY = TaskException("TASK_TITLE_IS_EMPTY", "Title is empty")
val TASK_DESCRIPTION_IS_EMPTY = TaskException("TASK_DESCRIPTION_IS_EMPTY", "Description is empty")
val TASK_RESPONSIBLE_IS_EMPTY = TaskException("TASK_RESPONSIBLE_IS_EMPTY", "Responsible is empty")
val TASK_RESPONSIBLE_NOT_FOUND = TaskException("TASK_RESPONSIBLE_NOT_FOUND", "Responsible not found")
val TASK_NOT_FOUND = TaskException("TASK_NOT_FOUND", "Task not found")
val TASK_ALREADY_COMPLETED = TaskException("TASK_ALREADY_COMPLETED", "Task already completed")

class TaskException(code: String, description: String) : DefaultError(code, description)