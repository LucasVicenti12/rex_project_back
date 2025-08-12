package com.delice.crm.modules.kanban.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.exceptions.KanbanExceptions

data class BoardResponse(
    val board: Board? = null,
    val error: KanbanExceptions? = null
)

data class CardResponse(
    val card: Card? = null,
    val error: KanbanExceptions? = null
)

data class ColumnResponse(
    val column: Column? = null,
    val error: KanbanExceptions? = null
)

data class TagResponse(
    val tag: Tag? = null,
    val error: KanbanExceptions? = null
)

data class CardListResponse(
    val cards: List<Card>? = listOf(),
    val error: KanbanExceptions? = null
)

data class MoveCardResponse(
    val move: Boolean,
    val error: KanbanExceptions? = null
)

data class ColumnListResponse(
    val columns: List<Column>? = listOf(),
    val error: KanbanExceptions? = null
)

data class TagListResponse(
    val tags: List<Tag>? = listOf(),
    val error: KanbanExceptions? = null
)

data class BoardPaginationResponse(
    val boards: Pagination<Board>? = null,
    val error: KanbanExceptions? = null
)

data class ColumnRuleResponse(
    val columnRule: ColumnRule? = null,
    val error: KanbanExceptions? = null
)

data class MessageBoardResponse(
    val message: String? = null,
    val error: KanbanExceptions? = null
)