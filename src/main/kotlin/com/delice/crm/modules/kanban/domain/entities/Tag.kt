package com.delice.crm.modules.kanban.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import java.time.LocalDateTime
import java.util.UUID

class Tag(
    var uuid: UUID? = null,
    var boardUUID: UUID? = null,
    var color: String? = null,
    var description: String? = null,
    var status: TagStatus? = TagStatus.ACTIVE,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

enum class TagStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1),
}