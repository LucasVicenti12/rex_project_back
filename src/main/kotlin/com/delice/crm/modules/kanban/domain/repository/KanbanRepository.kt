package com.delice.crm.modules.kanban.domain.repository

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.kanban.domain.entities.*
import java.util.UUID

interface KanbanRepository {
    fun registerBoard(board: Board): Board?
    fun registerCard(card: Card): Card?
    fun registerColumn(column: Column): Column?
    fun registerTag(tag: Tag): Tag?

    fun updateBoard(board: Board): Board?
    fun updateCard(card: Card): Card?
    fun updateColumn(column: Column): Column?
    fun updateTag(tag: Tag): Tag?

    fun getBoardByUUID(uuid: UUID): Board?
    fun getCardByUUID(uuid: UUID): Card?
    fun getColumnByUUID(uuid: UUID): Column?
    fun getTagByUUID(uuid: UUID): Tag?

    fun getBoardByCode(code: String): Board?
    fun getCardByCode(code: String): Card?
    fun getColumnByCode(code: String): Column?

    fun getCardsByBoardUUID(uuid: UUID): List<Card>?
    fun getColumnsByBoardUUID(uuid: UUID): List<Column>?
    fun getTagsByBoardUUID(uuid: UUID): List<Tag>?

    fun getBoardPagination(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): Pagination<Board>?

    fun deleteTagByUUID(tagUUID: UUID)
    fun deleteColumnByUUID(columnUUID: UUID)

    fun reorderColumns(columns: List<Column>): List<Column>?

    fun saveColumnRule(columnRule: ColumnRule): ColumnRule?
    fun getColumnRuleByUUID(columnRuleUUID: UUID): ColumnRule?
    fun getRulesByColumnUUID(columnUUID: UUID): List<ColumnRule>?

    fun saveAllowedColumns(columnUUID: UUID, allowed: List<UUID>)

    fun deleteAllowedColumnUUID(mainColumnUUID: UUID, columnUUID: UUID)
    fun deleteColumnRuleByUUID(ruleUUID: UUID)

    fun addTagToCard(cardUUID: UUID, tagUUID: UUID?)

    fun moveCardToColumn(cardUUID: UUID, columnUUID: UUID, boardUUID: UUID): List<Card>?

    fun getCardIndexByBoardUUID(boardUUID: UUID): Int?

    fun setDefaultColumn(boardUUID: UUID, columnUUID: UUID): List<Column>?

    fun deleteCardsByBoardUUID(boardUUID: UUID)
}