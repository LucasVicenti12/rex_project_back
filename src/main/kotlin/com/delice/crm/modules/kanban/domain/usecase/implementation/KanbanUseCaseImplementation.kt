package com.delice.crm.modules.kanban.domain.usecase.implementation

import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.exceptions.*
import com.delice.crm.modules.kanban.domain.repository.KanbanRepository
import com.delice.crm.modules.kanban.domain.usecase.KanbanUseCase
import com.delice.crm.modules.kanban.domain.usecase.response.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class KanbanUseCaseImplementation(
    private val kanbanRepository: KanbanRepository,
    private val userRepository: UserRepository
) : KanbanUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(KanbanUseCaseImplementation::class.java)
    }

    override fun saveBoard(board: Board): BoardResponse = try {
        validateBoard(board).let {
            when {
                it != null -> {
                    BoardResponse(error = it)
                }

                board.uuid != null -> {
                    BoardResponse(board = kanbanRepository.updateBoard(board))
                }

                else -> {
                    if (kanbanRepository.getBoardByCode(board.code!!) != null) {
                        BoardResponse(error = KANBAN_BOARD_ALREADY_EXISTS)
                    } else {
                        BoardResponse(board = kanbanRepository.registerBoard(board))
                    }
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SAVE_BOARD", e)
        BoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun saveCard(card: Card): CardResponse = try {
        validateCard(card).let {
            when {
                it != null -> {
                    CardResponse(error = it)
                }

                card.uuid != null -> {
                    CardResponse(card = kanbanRepository.updateCard(card))
                }

                else -> {
                    if (kanbanRepository.getCardByCode(card.code!!) != null) {
                        CardResponse(error = KANBAN_CARD_ALREADY_EXISTS)
                    } else {
                        CardResponse(card = kanbanRepository.registerCard(card))
                    }
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SAVE_CARD", e)
        CardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun saveColumn(column: Column): ColumnResponse = try {
        validateColumn(column).let {
            when {
                it != null -> {
                    ColumnResponse(error = it)
                }

                column.uuid != null -> {
                    ColumnResponse(column = kanbanRepository.updateColumn(column))
                }

                else -> {
                    if (kanbanRepository.getColumnByCode(column.code!!) != null) {
                        ColumnResponse(error = KANBAN_COLUMN_ALREADY_EXISTS)
                    } else {
                        ColumnResponse(column = kanbanRepository.registerColumn(column))
                    }
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SAVE_COLUMN", e)
        ColumnResponse(error = KANBAN_UNEXPECTED)
    }

    override fun saveTag(tag: Tag): TagResponse = try {
        validateTag(tag).let {
            when {
                it != null -> {
                    TagResponse(error = it)
                }

                tag.uuid != null -> {
                    TagResponse(tag = kanbanRepository.updateTag(tag))
                }

                else -> {
                    TagResponse(tag = kanbanRepository.registerTag(tag))
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SAVE_TAG", e)
        TagResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getBoardByUUID(uuid: UUID): BoardResponse = try {
        val board = kanbanRepository.getBoardByUUID(uuid)

        if (board == null) {
            BoardResponse(error = KANBAN_CARD_NOT_FOUND)
        } else {
            BoardResponse(board = board)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_BOARD_BY_UUID", e)
        BoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getCardByUUID(uuid: UUID): CardResponse = try {
        val card = kanbanRepository.getCardByUUID(uuid)

        if (card == null) {
            CardResponse(error = KANBAN_CARD_NOT_FOUND)
        } else {
            CardResponse(card = card)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_CARD_BY_UUID", e)
        CardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getColumnByUUID(uuid: UUID): ColumnResponse = try {
        val column = kanbanRepository.getColumnByUUID(uuid)

        if (column == null) {
            ColumnResponse(error = KANBAN_CARD_NOT_FOUND)
        } else {
            ColumnResponse(column = column)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_COLUMN_BY_UUID", e)
        ColumnResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getBoardByCode(code: String): BoardResponse = try {
        val board = kanbanRepository.getBoardByCode(code)

        if (board == null) {
            BoardResponse(error = KANBAN_CARD_NOT_FOUND)
        } else {
            BoardResponse(board = board)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_BOARD_BY_CODE", e)
        BoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getCardsByBoardUUID(uuid: UUID): CardListResponse = try {
        val cards = kanbanRepository.getCardsByBoardUUID(uuid)

        if (cards == null) {
            CardListResponse(error = KANBAN_CARD_NOT_FOUND)
        } else {
            CardListResponse(cards = cards)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_CARD_BY_BOARD_UUID", e)
        CardListResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getColumnsByBoardUUID(uuid: UUID): ColumnListResponse = try {
        val columns = kanbanRepository.getColumnsByBoardUUID(uuid)

        if (columns == null) {
            ColumnListResponse(error = KANBAN_COLUMN_NOT_FOUND)
        } else {
            ColumnListResponse(columns = columns)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_COLUMN_BY_BOARD_UUID", e)
        ColumnListResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getTagsByBoardUUID(uuid: UUID): TagListResponse = try {
        val tags = kanbanRepository.getTagsByBoardUUID(uuid)

        if (tags == null) {
            TagListResponse(error = KANBAN_TAG_NOT_FOUND)
        } else {
            TagListResponse(tags = tags)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_TAG_BY_BOARD_UUID", e)
        TagListResponse(error = KANBAN_UNEXPECTED)
    }

    override fun getBoardPagination(page: Int, count: Int, params: Map<String, Any?>): BoardPaginationResponse = try {
        val boards = kanbanRepository.getBoardPagination(page, count, params)

        BoardPaginationResponse(boards = boards)
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_BOARD_PAGINATION", e)
        BoardPaginationResponse(error = KANBAN_UNEXPECTED)
    }

    override fun deleteTagByUUID(tagUUID: UUID): MessageBoardResponse = try {
        val tag = kanbanRepository.getTagByUUID(tagUUID)

        if (tag == null) {
            MessageBoardResponse(error = KANBAN_TAG_NOT_FOUND)
        } else {
            kanbanRepository.deleteTagByUUID(tagUUID)
            MessageBoardResponse(message = "Tag deleted with success")
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_DELETE_TAG_BY_UUID", e)
        MessageBoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun deleteColumnByUUID(columnUUID: UUID): MessageBoardResponse = try {
        val tag = kanbanRepository.getColumnByUUID(columnUUID)

        if (tag == null) {
            MessageBoardResponse(error = KANBAN_COLUMN_NOT_FOUND)
        } else {
            kanbanRepository.deleteColumnByUUID(columnUUID)
            MessageBoardResponse(message = "Column deleted with success")
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_DELETE_COLUMN_BY_UUID", e)
        MessageBoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun reorderColumns(columns: List<Column>): ColumnListResponse = try {
        when {
            columns.map { it.index }.toSet().size != columns.size -> {
                ColumnListResponse(error = KANBAN_COLUMN_INDEX_REPEATED)
            }

            else -> {
                ColumnListResponse(
                    columns = kanbanRepository.reorderColumns(columns)
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_REORDER_COLUMNS", e)
        ColumnListResponse(error = KANBAN_UNEXPECTED)
    }

    override fun saveColumnRule(columnRule: ColumnRule): ColumnRuleResponse = try {
        validateColumnRule(columnRule).let {
            when {
                it != null -> {
                    ColumnRuleResponse(error = it)
                }

                else -> {
                    ColumnRuleResponse(
                        columnRule = kanbanRepository.saveColumnRule(columnRule)
                    )
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SAVE_COLUMN_RULE", e)
        ColumnRuleResponse(error = KANBAN_UNEXPECTED)
    }

    override fun saveAllowedColumns(columnUUID: UUID, allowed: List<UUID>): MessageBoardResponse = try {
        validateAllowedColumns(allowed).let {
            when {
                it != null -> {
                    MessageBoardResponse(error = it)
                }

                kanbanRepository.getColumnByUUID(columnUUID) == null -> {
                    MessageBoardResponse(error = KANBAN_COLUMN_NOT_FOUND)
                }

                else -> {
                    kanbanRepository.saveAllowedColumns(columnUUID, allowed)

                    MessageBoardResponse(message = "Allowed columns saved with success")
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SAVE_ALLOWED_COLUMNS", e)
        MessageBoardResponse(error = KANBAN_UNEXPECTED)
    }

    private fun validateBoard(board: Board): KanbanExceptions? = when {
        board.code.isNullOrBlank() -> {
            KANBAN_CODE_IS_EMPTY
        }

        board.title.isNullOrEmpty() -> {
            KANBAN_TITLE_IS_EMPTY
        }

        else -> {
            null
        }
    }

    private fun validateCard(card: Card): KanbanExceptions? = when {
        card.code.isNullOrBlank() -> {
            KANBAN_CODE_IS_EMPTY
        }

        card.title.isNullOrEmpty() -> {
            KANBAN_TITLE_IS_EMPTY
        }

        card.boardUUID == null -> {
            KANBAN_CARD_BOARD_UUID_IS_EMPTY
        }

        card.columnUUID == null -> {
            KANBAN_CARD_COLUMN_UUID_IS_EMPTY
        }

        kanbanRepository.getBoardByUUID(card.boardUUID!!) == null -> {
            KANBAN_BOARD_NOT_FOUND
        }

        else -> {
            kanbanRepository.getColumnByUUID(card.columnUUID!!).let {
                when {
                    it == null -> {
                        KANBAN_COLUMN_NOT_FOUND
                    }

                    it.allowedColumns!!.isNotEmpty() && !it.allowedColumns!!.contains(card.columnUUID!!) -> {
                        KANBAN_CANNOT_DO_ACTION
                    }

                    else -> {
                        null
                    }
                }
            }
        }
    }

    private fun validateColumn(column: Column): KanbanExceptions? = when {
        column.code.isNullOrBlank() -> {
            KANBAN_CODE_IS_EMPTY
        }

        column.title.isNullOrEmpty() -> {
            KANBAN_TITLE_IS_EMPTY
        }

        column.boardUUID == null -> {
            KANBAN_COLUMN_BOARD_UUID_IS_EMPTY
        }

        else -> {
            null
        }
    }

    private fun validateTag(tag: Tag): KanbanExceptions? = when {
        tag.description.isNullOrBlank() -> {
            KANBAN_TAG_DESCRIPTION_IS_EMPTY
        }

        tag.color.isNullOrEmpty() -> {
            KANBAN_TAG_COLOR_IS_EMPTY
        }

        tag.boardUUID == null -> {
            KANBAN_TAG_BOARD_UUID_IS_EMPTY
        }

        tag.title.isNullOrBlank() -> {
            KANBAN_TITLE_IS_EMPTY
        }

        else -> {
            null
        }
    }

    private fun validateColumnRule(columnRule: ColumnRule): KanbanExceptions? {
        var validate = when {
            columnRule.type == null -> {
                KANBAN_COLUMN_RULE_TYPE_IS_EMPTY
            }

            columnRule.title.isNullOrBlank() -> {
                KANBAN_TITLE_IS_EMPTY
            }

            else -> {
                null
            }
        }

        if (validate != null) {
            return validate
        }

        when (columnRule.type) {
            ColumnRuleType.SEND_EMAIL -> {
                if (columnRule.metadata == null) {
                    validate = KANBAN_COLUMN_RULE_DATA_INVALID
                } else {
                    if (columnRule.metadata!!.emails.isNullOrEmpty()) {
                        validate = KANBAN_COLUMN_RULE_EMAIL_IS_EMPTY
                    }
                }
            }

            ColumnRuleType.NOTIFY_USER -> {
                if (columnRule.metadata == null) {
                    validate = KANBAN_COLUMN_RULE_DATA_INVALID
                } else {
                    if (columnRule.metadata!!.notifyUsers.isNullOrEmpty()) {
                        validate = KANBAN_COLUMN_RULE_USER_IS_EMPTY
                    } else {
                        columnRule.metadata!!.notifyUsers!!.forEach {
                            val user = userRepository.getUserByUUID(UUID.fromString(it))

                            if (user == null) {
                                validate = KANBAN_USER_NOT_FOUND
                            }
                        }
                    }
                }
            }

            ColumnRuleType.ADD_TAG -> {
                if (columnRule.metadata == null) {
                    validate = KANBAN_COLUMN_RULE_DATA_INVALID
                } else {
                    if (columnRule.metadata!!.tag.isNullOrBlank()) {
                        validate = KANBAN_COLUMN_RULE_TAG_IS_EMPTY
                    } else {
                        val tag = kanbanRepository.getTagByUUID(UUID.fromString(columnRule.metadata!!.tag))

                        if (tag == null) {
                            validate = KANBAN_TAG_NOT_FOUND
                        }
                    }
                }
            }

            else -> {
                if (columnRule.metadata != null) {
                    validate = KANBAN_COLUMN_RULE_DATA_INVALID
                }
            }
        }

        return validate
    }

    private fun validateAllowedColumns(columns: List<UUID>): KanbanExceptions? = when {
        columns.isEmpty() -> {
            KANBAN_ALLOWED_COLUMNS_IS_EMPTY
        }

        else -> {
            var validate: KanbanExceptions? = null

            columns.forEach {
                val column = kanbanRepository.getColumnByUUID(it)

                if (column == null) {
                    validate = KANBAN_COLUMN_NOT_FOUND
                }
            }

            validate
        }
    }
}