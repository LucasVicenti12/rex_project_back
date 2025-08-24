package com.delice.crm.modules.kanban.domain.usecase.implementation

import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.queue.MailQueue
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.formatter.DateTimeFormat
import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.exceptions.*
import com.delice.crm.modules.kanban.domain.repository.KanbanRepository
import com.delice.crm.modules.kanban.domain.usecase.KanbanUseCase
import com.delice.crm.modules.kanban.domain.usecase.response.*
import com.delice.crm.modules.wallet.domain.repository.WalletRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class KanbanUseCaseImplementation(
    private val kanbanRepository: KanbanRepository,
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val mailQueue: MailQueue
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

    override fun getColumnRuleByUUID(uuid: UUID): ColumnRuleResponse = try {
        val rule = kanbanRepository.getColumnRuleByUUID(uuid)

        if (rule == null) {
            ColumnRuleResponse(error = KANBAN_COLUMN_RULE_NOT_FOUND)
        } else {
            ColumnRuleResponse(columnRule = rule)
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_COLUMN_RULE_BY_UUID", e)
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

    override fun deleteAllowedColumnUUID(mainColumnUUID: UUID, columnUUID: UUID): MessageBoardResponse = try {
        val column = kanbanRepository.getColumnByUUID(columnUUID)

        if (column == null) {
            MessageBoardResponse(error = KANBAN_TAG_NOT_FOUND)
        } else {
            kanbanRepository.deleteAllowedColumnUUID(mainColumnUUID, columnUUID)
            MessageBoardResponse(message = "Allowed column deleted with success")
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_DELETE_ALLOWED_COLUMN_BY_UUID", e)
        MessageBoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun deleteColumnRuleByUUID(ruleUUID: UUID): MessageBoardResponse = try {
        val rule = kanbanRepository.getColumnRuleByUUID(ruleUUID)

        if (rule == null) {
            MessageBoardResponse(error = KANBAN_TAG_NOT_FOUND)
        } else {
            kanbanRepository.deleteColumnRuleByUUID(ruleUUID)
            MessageBoardResponse(message = "Allowed column deleted with success")
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_DELETE_RULE_COLUMN_BY_UUID", e)
        MessageBoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun setDefaultColumn(boardUUID: UUID, columnUUID: UUID): ColumnListResponse = try {
        val column = kanbanRepository.getColumnByUUID(columnUUID)

        if (column == null) {
            ColumnListResponse(error = KANBAN_COLUMN_NOT_FOUND)
        } else {
            ColumnListResponse(
                columns = kanbanRepository.setDefaultColumn(
                    boardUUID = boardUUID,
                    columnUUID = columnUUID
                )
            )
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_SET_DEFAULT_COLUMN", e)
        ColumnListResponse(error = KANBAN_UNEXPECTED)
    }

    override fun recreateCards(boardUUID: UUID): MessageBoardResponse = try {
        kanbanRepository.deleteCardsByBoardUUID(boardUUID)

        val customers = customerRepository.getCustomerAll()

        customers!!.forEach {
            val card = customerRepository.createCustomerCardKanban(it)
            val columnUUID = customerRepository.getKanbanColumnUUIDByCustomerStatus(it.status!!)

            if (card != null && columnUUID != null) {
                moveCardToColumn(card.uuid!!, columnUUID)
            }
        }

        MessageBoardResponse(message = "Cards recreated with success")
    } catch (e: Exception) {
        logger.error("ERROR_IN_RECREATE_CARDS", e)
        MessageBoardResponse(error = KANBAN_UNEXPECTED)
    }

    override fun moveCardToColumn(cardUUID: UUID, toColumnUUID: UUID): CardListResponse {
        try {
            val user = getCurrentUser().getUserData()

            val card = kanbanRepository.getCardByUUID(cardUUID) ?: return CardListResponse(
                error = KANBAN_CARD_NOT_FOUND
            )

            val column = kanbanRepository.getColumnByUUID(toColumnUUID) ?: return CardListResponse(
                error = KANBAN_COLUMN_NOT_FOUND
            )

            if (card.columnUUID == toColumnUUID) {
                return CardListResponse(
                    error = KANBAN_COLUMN_MOVE_IN_SAME_COLUMN
                )
            }

            if (!column.allowedColumns!!.contains(card.columnUUID)) {
                return CardListResponse(
                    error = KANBAN_CANNOT_DO_ACTION
                )
            }

            val currentColumn = kanbanRepository.getColumnByUUID(card.columnUUID!!) ?: return CardListResponse(
                error = KANBAN_COLUMN_NOT_FOUND
            )

            val currentColumnRule = kanbanRepository.getRulesByColumnUUID(currentColumn.uuid!!)

            var validate = applyCurrentColumnRuleValidation(
                rules = currentColumnRule!!
            )

            if (validate != null) {
                return CardListResponse(error = validate)
            }

            val newColumnRule = kanbanRepository.getRulesByColumnUUID(column.uuid!!)

            validate = applyNewColumnRuleValidation(
                card = card,
                rules = newColumnRule!!,
            )

            if (validate != null) {
                return CardListResponse(error = validate)
            }

            applyColumnRuleAction(
                card = card,
                column = column,
                user = user,
                rules = newColumnRule,
            )

            return CardListResponse(
                cards = kanbanRepository.moveCardToColumn(
                    cardUUID = card.uuid!!,
                    columnUUID = column.uuid!!,
                    boardUUID = column.boardUUID!!
                )
            )
        } catch (e: Exception) {
            logger.error("ERROR_IN_MOVE_CARD_TO_COLUMN", e)
            return CardListResponse(error = KANBAN_UNEXPECTED)
        }
    }

    override fun getColumnRuleTypes(): ColumnRuleTypeListResponse = try {
        ColumnRuleTypeListResponse(
            rules = ColumnRuleType.entries.toList()
        )
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_COLUMN_RULE_TYPES", e)
        ColumnRuleTypeListResponse(error = KANBAN_UNEXPECTED)
    }

    private fun applyCurrentColumnRuleValidation(rules: List<ColumnRule>): KanbanExceptions? {
        var move: KanbanExceptions? = null

        if (rules.isEmpty()) return null

        rules.forEach {
            if (ColumnRuleType.NOT_MOVABLE == it.type) {
                move = KANBAN_RULE_NOT_MOVE
                return@forEach
            }
        }

        return move
    }

    private fun applyNewColumnRuleValidation(card: Card, rules: List<ColumnRule>): KanbanExceptions? {
        var move: KanbanExceptions? = null

        if (rules.isEmpty()) return null

        rules.forEach {
            if (ColumnRuleType.VALIDATE_CUSTOMER == it.type) {
                val customer = card.metadata!!.customer

                if (customer == null) {
                    move = KANBAN_RULE_CUSTOMER_NOT_FIT
                    return@forEach
                }

                if (customer.status != CustomerStatus.FIT) {
                    move = KANBAN_RULE_CUSTOMER_NOT_FIT
                    return@forEach
                }
            }
            if (ColumnRuleType.VALIDATE_CUSTOMER_WALLET == it.type) {
                val wallet = card.metadata!!.wallet

                if (wallet == null) {
                    move = KANBAN_RULE_CUSTOMER_WITHOUT_WALLET
                    return@forEach
                }
            }
        }

        return move
    }

    private fun applyColumnRuleAction(
        card: Card,
        column: Column,
        user: User,
        rules: List<ColumnRule>
    ) {
        if (rules.isEmpty()) return

        rules.forEach {
            if (ColumnRuleType.SEND_EMAIL == it.type || ColumnRuleType.NOTIFY_USER == it.type) {
                val mails = it.metadata!!.emails!!.joinToString(";")

                val date = LocalDateTime.now().format(DateTimeFormat)

                val mail = Mail(
                    subject = CARD_MOVE_MESSAGE.format(
                        card.code,
                        column.title,
                        user.name
                    ),
                    content = CARD_CONTENT_EMAIL.format(
                        card.code,
                        card.title,
                        card.description,
                        date,
                        column.title
                    ),
                    to = mails,
                    withHtml = true
                )

                mailQueue.addMail(
                    mail = mail
                )
            }

            if (ColumnRuleType.ADD_TAG == it.type) {
                val tag = kanbanRepository.getTagByUUID(
                    uuid = UUID.fromString(it.metadata!!.tag)
                )

                kanbanRepository.addTagToCard(
                    cardUUID = card.uuid!!,
                    tagUUID = tag!!.uuid
                )
            }

            if (ColumnRuleType.REMOVE_TAG == it.type) {
                kanbanRepository.addTagToCard(
                    cardUUID = card.uuid!!,
                    tagUUID = null
                )
            }

            if (ColumnRuleType.APPROVE_CUSTOMER == it.type) {
                customerRepository.approvalCustomer(
                    status = CustomerStatus.FIT,
                    customerUUID = card.metadata!!.customer!!.uuid!!,
                    userUUID = user.uuid!!
                )
            }

            if (ColumnRuleType.REPROVE_CUSTOMER == it.type) {
                customerRepository.approvalCustomer(
                    status = CustomerStatus.NOT_FIT,
                    customerUUID = card.metadata!!.customer!!.uuid!!,
                    userUUID = user.uuid!!
                )
            }

            if (ColumnRuleType.REVIEW_CUSTOMER == it.type) {
                customerRepository.approvalCustomer(
                    status = CustomerStatus.PENDING,
                    customerUUID = card.metadata!!.customer!!.uuid!!,
                    userUUID = user.uuid!!
                )
            }
        }
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