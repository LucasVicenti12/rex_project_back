package com.delice.crm.modules.kanban.domain.usecase

import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.usecase.response.*
import java.util.*

interface KanbanUseCase {
    fun saveBoard(board: Board): BoardResponse
    fun saveCard(card: Card): CardResponse
    fun saveColumn(column: Column): ColumnResponse
    fun saveTag(tag: Tag): TagResponse

    fun getBoardByUUID(uuid: UUID): BoardResponse
    fun getCardByUUID(uuid: UUID): CardResponse
    fun getColumnByUUID(uuid: UUID): ColumnResponse

    fun getBoardByCode(code: String): BoardResponse

    fun getCardsByBoardUUID(uuid: UUID): CardListResponse
    fun getColumnsByBoardUUID(uuid: UUID): ColumnListResponse
    fun getTagsByBoardUUID(uuid: UUID): TagListResponse

    fun getBoardPagination(page: Int, count: Int, params: Map<String, Any?>): BoardPaginationResponse

    fun deleteTagByUUID(tagUUID: UUID): MessageBoardResponse
    fun deleteColumnByUUID(columnUUID: UUID): MessageBoardResponse

    fun reorderColumns(columns: List<Column>): ColumnListResponse

    fun saveColumnRule(columnRule: ColumnRule): ColumnRuleResponse
    fun getColumnRuleByUUID(uuid: UUID): ColumnRuleResponse

    fun saveAllowedColumns(columnUUID: UUID, allowed: List<UUID>): MessageBoardResponse

    fun deleteAllowedColumnUUID(mainColumnUUID: UUID, columnUUID: UUID): MessageBoardResponse
    fun deleteColumnRuleByUUID(ruleUUID: UUID): MessageBoardResponse

    fun validateMoveCardToColumn(cardUUID: UUID, toColumnUUID: UUID): CardListResponse

    fun setDefaultColumn(boardUUID: UUID, columnUUID: UUID): ColumnListResponse
}