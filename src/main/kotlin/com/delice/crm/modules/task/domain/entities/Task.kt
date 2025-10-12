package com.delice.crm.modules.task.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import java.time.LocalDateTime
import java.util.UUID
import com.delice.crm.core.user.domain.entities.User

class Task (
    val uuid: UUID? = null,
    val title: String? = null,
    val description: String? = null,
    val responsible: User? = null,
    val status: TaskStatus? = TaskStatus.PENDING,
    val priority: TaskPriority? = TaskPriority.MEDIUM,
    val dueDate: LocalDateTime? = LocalDateTime.now(),
    val history: List<TaskHistory> = listOf(),
    var createdBy: User? = null,
    var modifiedBy: User? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

class TaskHistory(
    val uuid: UUID? = null,
    val taskUUID: UUID? = null,
    val description: String? = null,
    val action: TaskAction? = TaskAction.UPDATED,
    var actionBy: User? = null,
    val actionAt: LocalDateTime? = LocalDateTime.now(),
)

enum class TaskStatus(override val code: Int): HasCode {
    PENDING(0),
    RUNNING(1),
    COMPLETED(2),
}

enum class TaskPriority(override val code: Int): HasCode {
    LOWEST(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    HIGHEST(4);
}

enum class TaskAction(override val code: Int): HasCode {
    CREATED(0),
    UPDATED(1),
    FINISHED(2);
}