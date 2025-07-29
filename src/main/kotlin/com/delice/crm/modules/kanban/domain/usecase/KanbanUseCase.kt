package com.delice.crm.modules.kanban.domain.usecase

import com.delice.crm.modules.kanban.domain.entities.Board
import com.delice.crm.modules.kanban.domain.entities.Card
import com.delice.crm.modules.kanban.domain.entities.Column
import com.delice.crm.modules.kanban.domain.entities.Tag
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
}