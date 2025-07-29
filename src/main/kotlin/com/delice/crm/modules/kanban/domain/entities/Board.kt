package com.delice.crm.modules.kanban.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import java.time.LocalDateTime
import java.util.UUID

class Board (
    var uuid: UUID? = null,
    var code: String? = null,
    var title: String? = null,
    var description: String? = null,
    var cards: List<Card>? = listOf(),
    var columns: List<Column>? = listOf(),
    var tags: List<Tag>? = listOf(),
    var status: BoardStatus? = BoardStatus.ACTIVE,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

enum class BoardStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1),
}