package com.delice.crm.modules.kanban.infra.repository

import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.queue.MailQueue
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.formatter.DateTimeFormat
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.entities.Column
import com.delice.crm.modules.kanban.domain.entities.ColumnRule
import com.delice.crm.modules.kanban.domain.entities.ColumnType
import com.delice.crm.modules.kanban.domain.repository.KanbanRepository
import com.delice.crm.modules.kanban.infra.database.*
import com.delice.crm.modules.wallet.domain.repository.WalletRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ceil

@Service
class KanbanRepositoryImplementation(
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository,
    private val mailQueue: MailQueue
) : KanbanRepository {
    override fun registerBoard(board: Board): Board? = transaction {
        val uuid = UUID.randomUUID()

        BoardDatabase.insert {
            it[BoardDatabase.uuid] = uuid
            it[code] = board.code!!
            it[title] = board.title!!
            it[description] = board.description
            it[status] = board.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getBoardByUUID(uuid)
    }

    override fun registerCard(card: Card): Card? = transaction {
        val uuid = UUID.randomUUID()

        CardDatabase.insert {
            it[CardDatabase.uuid] = uuid
            it[columnUUID] = card.columnUUID!!
            it[boardUUID] = card.boardUUID!!
            it[code] = card.code!!
            it[title] = card.title!!
            it[description] = card.description
            it[status] = card.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()

            if (card.cardBaseMetadata != null) {
                it[metadata] = card.cardBaseMetadata
            }
        }

        return@transaction getCardByUUID(uuid)
    }

    override fun registerColumn(column: Column): Column? = transaction {
        val uuid = UUID.randomUUID()

        val lastIndex = getColumnLastIndex() ?: 0

        val defaultColumnCount = ColumnDatabase.select(ColumnDatabase.isDefault).where({
            ColumnDatabase.boardUUID eq column.boardUUID!! and (ColumnDatabase.isDefault eq true)
        }).count().toInt()

        if(defaultColumnCount == 0){
            column.isDefault = true
        }

        ColumnDatabase.insert {
            it[ColumnDatabase.uuid] = uuid
            it[code] = column.code!!
            it[boardUUID] = column.boardUUID!!
            it[title] = column.title!!
            it[description] = column.description
            it[status] = column.status!!.code
            it[index] = lastIndex
            it[type] = column.type!!.type
            it[isDefault] = column.isDefault!!
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
        }

        saveAllowedColumns(uuid, column.allowedColumns!!)

        return@transaction getColumnByUUID(uuid)
    }

    override fun registerTag(tag: Tag): Tag? = transaction {
        val uuid = UUID.randomUUID()

        TagDatabase.insert {
            it[TagDatabase.uuid] = uuid
            it[boardUUID] = tag.boardUUID!!
            it[color] = tag.color!!
            it[title] = tag.title!!
            it[description] = tag.description!!
            it[status] = tag.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getTagByUUID(uuid)
    }

    override fun updateBoard(board: Board): Board? = transaction {
        BoardDatabase.update({ BoardDatabase.uuid eq board.uuid!! }) {
            it[title] = board.title!!
            it[description] = board.description
            it[status] = board.status!!.code
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getBoardByUUID(board.uuid!!)
    }

    override fun updateCard(card: Card): Card? = transaction {
        CardDatabase.update({ CardDatabase.uuid eq card.uuid!! }) {
            it[columnUUID] = card.columnUUID!!
            it[title] = card.title!!
            it[description] = card.description
            it[status] = card.status!!.code
            it[modifiedAt] = LocalDateTime.now()

            if (card.cardBaseMetadata != null) {
                it[metadata] = card.cardBaseMetadata
            }
        }

        return@transaction getCardByUUID(card.uuid!!)
    }

    override fun updateColumn(column: Column): Column? = transaction {
        ColumnDatabase.update({ ColumnDatabase.uuid eq column.uuid!! }) {
            it[title] = column.title!!
            it[description] = column.description
            it[status] = column.status!!.code
            it[type] = column.type!!.type
            it[isDefault] = column.isDefault!!
            it[modifiedAt] = LocalDateTime.now()
        }

        saveAllowedColumns(column.uuid!!, column.allowedColumns!!)

        return@transaction getColumnByUUID(column.uuid!!)
    }

    override fun updateTag(tag: Tag): Tag? = transaction {
        TagDatabase.update({ TagDatabase.uuid eq tag.uuid!! }) {
            it[color] = tag.color!!
            it[title] = tag.title!!
            it[description] = tag.description!!
            it[status] = tag.status!!.code
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getTagByUUID(tag.uuid!!)
    }

    override fun getBoardByUUID(uuid: UUID): Board? = transaction {
        val board = BoardDatabase.selectAll().where { BoardDatabase.uuid eq uuid }.map {
            resultRowToBoard(it)
        }.firstOrNull()

        if (board != null) {
            board.columns = getColumnsByBoardUUID(board.uuid!!)
            board.tags = getTagsByBoardUUID(board.uuid!!)
        }

        return@transaction board
    }

    override fun getCardByUUID(uuid: UUID): Card? = transaction {
        val card = CardDatabase.selectAll().where { CardDatabase.uuid eq uuid }.map {
            resultRowToCard(it)
        }.firstOrNull()

        return@transaction card
    }

    override fun getColumnByUUID(uuid: UUID): Column? = transaction {
        val column = ColumnDatabase.selectAll().where { ColumnDatabase.uuid eq uuid }.map {
            val column = resultRowToColumn(it)

            column.allowedColumns = getAllowedColumns(column.uuid!!)

            column
        }.firstOrNull()

        return@transaction column
    }

    override fun getTagByUUID(uuid: UUID): Tag? = transaction {
        val tag = TagDatabase.selectAll().where { TagDatabase.uuid eq uuid }.map {
            resultRowToTag(it)
        }.firstOrNull()

        return@transaction tag
    }

    override fun getBoardByCode(code: String): Board? = transaction {
        val board = BoardDatabase.selectAll().where { BoardDatabase.code eq code }.map {
            resultRowToBoard(it)
        }.firstOrNull()

        if (board != null) {
            board.columns = getColumnsByBoardUUID(board.uuid!!)
            board.cards = getCardsByBoardUUID(board.uuid!!)
            board.tags = getTagsByBoardUUID(board.uuid!!)
        }

        return@transaction board
    }

    override fun getCardByCode(code: String): Card? = transaction {
        val card = CardDatabase.selectAll().where { CardDatabase.code eq code }.map {
            resultRowToCard(it)
        }.firstOrNull()

        return@transaction card
    }

    override fun getColumnByCode(code: String): Column? = transaction {
        val column = ColumnDatabase.selectAll().where { ColumnDatabase.code eq code }.map {
            resultRowToColumn(it)
        }.firstOrNull()

        return@transaction column
    }

    override fun getCardsByBoardUUID(uuid: UUID): List<Card>? = transaction {
        val cards = CardDatabase.selectAll().where {
            CardDatabase.boardUUID eq uuid and (CardDatabase.status eq CardStatus.ACTIVE.code)
        }.map {
            val card = resultRowToCard(it)

            if (card.cardBaseMetadata != null) {
                if (card.cardBaseMetadata!!.customer != null) {
                    val customer = customerRepository.getCustomerByUUID(
                        UUID.fromString(card.cardBaseMetadata!!.customer!!.uuid!!)
                    )

                    if (customer != null) {
                        val wallet = walletRepository.getCustomerWallet(
                            customerUUID = customer.uuid!!,
                            walletUUID = null
                        )

                        card.metadata = CardMetadata(
                            customer = customer,
                            wallet = wallet
                        )
                    }
                }
            }

            if (it[CardDatabase.tagUUID] != null) {
                card.tag = it[CardDatabase.tagUUID]?.let { tagUUID -> getTagByUUID(tagUUID) }
            }

            card
        }

        return@transaction cards
    }

    override fun getColumnsByBoardUUID(uuid: UUID): List<Column>? = transaction {
        val column = ColumnDatabase.selectAll().where {
            ColumnDatabase.boardUUID eq uuid and (ColumnDatabase.status eq ColumnStatus.ACTIVE.code)
        }.orderBy(
            column = ColumnDatabase.index,
            order = SortOrder.ASC
        ).map {
            val column = resultRowToColumn(it)

            column.rules = getRulesByColumnUUID(column.uuid!!)
            column.allowedColumns = getAllowedColumns(column.uuid!!)

            column
        }

        return@transaction column
    }

    override fun getTagsByBoardUUID(uuid: UUID): List<Tag>? = transaction {
        val tag = TagDatabase.selectAll().where {
            TagDatabase.boardUUID eq uuid and (TagDatabase.status eq TagStatus.ACTIVE.code)
        }.map {
            resultRowToTag(it)
        }

        return@transaction tag
    }

    override fun getBoardPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<Board>? =
        transaction {
            val query = BoardDatabase
                .selectAll()
                .where(BoardFilter(params).toFilter(BoardDatabase))

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .orderBy(column = BoardDatabase.modifiedAt, order = SortOrder.DESC)
                .limit(count)
                .offset((page * count).toLong())
                .map { resultRowToBoard(it) }

            Pagination(
                items = items,
                page = page,
                total = total
            )
        }

    override fun deleteTagByUUID(tagUUID: UUID) {
        transaction {
            TagDatabase.deleteWhere { uuid eq tagUUID }
        }
    }

    override fun deleteColumnByUUID(columnUUID: UUID) {
        transaction {
            ColumnDatabase.deleteWhere { uuid eq columnUUID }
        }
    }

    override fun reorderColumns(columns: List<Column>): List<Column>? = transaction {
        val columnsResponse = mutableListOf<Column>()

        columns.forEach { column ->
            ColumnDatabase.update({ ColumnDatabase.uuid eq column.uuid!! }) {
                it[index] = column.index!!
            }

            val columnResponse = getColumnByUUID(column.uuid!!)

            if (columnResponse != null) {
                columnsResponse.add(columnResponse)
            }
        }

        return@transaction columnsResponse
    }

    override fun saveColumnRule(columnRule: ColumnRule): ColumnRule? = transaction {
        val uuid = UUID.randomUUID()

        ColumnRuleDatabase.insert {
            it[ColumnRuleDatabase.uuid] = uuid
            it[columnUUID] = columnRule.columnUUID!!
            it[title] = columnRule.title!!
            it[type] = columnRule.type!!.type
            it[metadata] = columnRule.metadata
            it[TagDatabase.createdAt] = LocalDateTime.now()
            it[TagDatabase.modifiedAt] = LocalDateTime.now()
        }

        return@transaction getColumnRuleByUUID(uuid)
    }

    override fun getColumnRuleByUUID(columnRuleUUID: UUID): ColumnRule? = transaction {
        ColumnRuleDatabase.selectAll().where { ColumnRuleDatabase.uuid eq columnRuleUUID }.map {
            resultRowToColumnRule(it)
        }.firstOrNull()
    }

    override fun getRulesByColumnUUID(columnUUID: UUID): List<ColumnRule>? = transaction {
        ColumnRuleDatabase.selectAll().where { ColumnRuleDatabase.columnUUID eq columnUUID }.map {
            resultRowToColumnRule(it)
        }
    }

    override fun saveAllowedColumns(columnUUID: UUID, allowed: List<UUID>) {
        transaction {
            ColumnAllowedDatabase.deleteWhere {
                ColumnAllowedDatabase.columnUUID eq columnUUID
            }

            allowed.forEach { column ->
                val uuid = UUID.randomUUID()

                ColumnAllowedDatabase.insert {
                    it[ColumnAllowedDatabase.uuid] = uuid
                    it[ColumnAllowedDatabase.columnUUID] = columnUUID
                    it[allowedColumnUUID] = column
                }
            }

            val selfColumnUUID = UUID.randomUUID()

            ColumnAllowedDatabase.insert {
                it[uuid] = selfColumnUUID
                it[ColumnAllowedDatabase.columnUUID] = columnUUID
                it[allowedColumnUUID] = columnUUID
            }
        }
    }

    override fun deleteAllowedColumnUUID(mainColumnUUID: UUID, columnUUID: UUID) {
        transaction {
            ColumnAllowedDatabase.deleteWhere {
                allowedColumnUUID eq columnUUID and (ColumnAllowedDatabase.columnUUID eq mainColumnUUID)
            }
        }
    }

    override fun deleteColumnRuleByUUID(ruleUUID: UUID) {
        transaction {
            ColumnRuleDatabase.deleteWhere {
                uuid eq ruleUUID
            }
        }
    }

    override fun addTagToCard(cardUUID: UUID, tagUUID: UUID?) {
        transaction {
            CardDatabase.update({ CardDatabase.uuid eq cardUUID }) {
                it[CardDatabase.tagUUID] = tagUUID
            }
        }
    }

    override fun applyRulesOnMoveEnd(card: Card, column: Column, user: User, rules: List<ColumnRule>): Boolean {
        var move = true

        transaction {
            rules.forEach { rule ->
                when (rule.type) {
                    ColumnRuleType.SEND_EMAIL, ColumnRuleType.NOTIFY_USER -> {
                        val mails = rule.metadata!!.emails!!.joinToString(";")

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

                    ColumnRuleType.ADD_TAG -> {
                        val tag = getTagByUUID(
                            UUID.fromString(rule.metadata!!.tag)
                        )

                        addTagToCard(
                            cardUUID = card.uuid!!,
                            tagUUID = tag!!.uuid
                        )
                    }

                    ColumnRuleType.REMOVE_TAG -> {
                        addTagToCard(
                            cardUUID = card.uuid!!,
                            tagUUID = null
                        )
                    }

                    ColumnRuleType.VALIDATE_CUSTOMER -> {
                        val customerUUID = UUID.fromString(card.cardBaseMetadata!!.customer!!.uuid)

                        val customer = customerRepository.getCustomerByUUID(customerUUID)

                        if (customer!!.status != CustomerStatus.FIT) {
                            move = false
                        }
                    }

                    ColumnRuleType.VALIDATE_CUSTOMER_WALLET -> {
                        val customerUUID = UUID.fromString(card.cardBaseMetadata!!.customer!!.uuid)

                        val wallet = walletRepository.getCustomerWallet(
                            customerUUID = customerUUID,
                            walletUUID = null,
                        )

                        if (wallet == null) {
                            move = false
                        }
                    }

                    ColumnRuleType.APPROVE_CUSTOMER -> {
                        val customerUUID = UUID.fromString(card.cardBaseMetadata!!.customer!!.uuid)

                        customerRepository.approvalCustomer(
                            status = CustomerStatus.FIT,
                            customerUUID = customerUUID,
                            userUUID = user.uuid!!
                        )
                    }

                    ColumnRuleType.REPROVE_CUSTOMER -> {
                        val customerUUID = UUID.fromString(card.cardBaseMetadata!!.customer!!.uuid)

                        customerRepository.approvalCustomer(
                            status = CustomerStatus.NOT_FIT,
                            customerUUID = customerUUID,
                            userUUID = user.uuid!!
                        )
                    }

                    ColumnRuleType.REVIEW_CUSTOMER -> {
                        val customerUUID = UUID.fromString(card.cardBaseMetadata!!.customer!!.uuid)

                        customerRepository.approvalCustomer(
                            status = CustomerStatus.PENDING,
                            customerUUID = customerUUID,
                            userUUID = user.uuid!!
                        )
                    }

                    else -> {}
                }
            }

            if (!move) {
                rollback()
            }
        }

        return move
    }

    override fun applyRulesOnMoveStart(card: Card, column: Column, user: User, rules: List<ColumnRule>): Boolean {
        var move = true

        transaction {
            rules.forEach { rule ->
                if (ColumnRuleType.NOT_MOVABLE == rule.type) {
                    move = false
                }
            }

            if (!move) {
                rollback()
            }
        }

        return move
    }

    override fun moveCardToColumn(cardUUID: UUID, columnUUID: UUID, boardUUID: UUID): List<Card>? = transaction {
        CardDatabase.update({ CardDatabase.uuid eq cardUUID }) {
            it[CardDatabase.columnUUID] = columnUUID
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getCardsByBoardUUID(boardUUID)
    }

    override fun getCardIndexByBoardUUID(boardUUID: UUID): Int? = transaction {
        CardDatabase.select(CardDatabase.uuid).where({ CardDatabase.boardUUID eq boardUUID }).count().toInt() + 1
    }

    override fun setDefaultColumn(boardUUID: UUID, columnUUID: UUID): List<Column>? = transaction {
        ColumnDatabase.update({ ColumnDatabase.boardUUID eq boardUUID }) {
            it[isDefault] = false
        }

        ColumnDatabase.update({ ColumnDatabase.uuid eq columnUUID }) {
            it[isDefault] = true
        }

        return@transaction getColumnsByBoardUUID(boardUUID)
    }

    private fun getAllowedColumns(columnUUID: UUID): List<UUID> = transaction {
        ColumnAllowedDatabase.select(ColumnAllowedDatabase.allowedColumnUUID).where {
            ColumnAllowedDatabase.columnUUID eq columnUUID
        }.map {
            it[ColumnAllowedDatabase.allowedColumnUUID]
        }
    }

    private fun getColumnLastIndex(): Int? = transaction {
        ColumnDatabase.select(ColumnDatabase.index).where {
            ColumnDatabase.status eq ColumnStatus.ACTIVE.code
        }.orderBy(ColumnDatabase.index, SortOrder.DESC)
            .limit(count = 1)
            .map { it[ColumnDatabase.index] }
            .firstOrNull()
    }

    private fun resultRowToBoard(it: ResultRow): Board = Board(
        uuid = it[BoardDatabase.uuid],
        code = it[BoardDatabase.code],
        title = it[BoardDatabase.title],
        description = it[BoardDatabase.description],
        status = enumFromTypeValue<BoardStatus, Int>(it[BoardDatabase.status]),
        createdAt = it[BoardDatabase.createdAt],
        modifiedAt = it[BoardDatabase.modifiedAt],
    )

    private fun resultRowToCard(it: ResultRow): Card = Card(
        uuid = it[CardDatabase.uuid],
        boardUUID = it[CardDatabase.boardUUID],
        columnUUID = it[CardDatabase.columnUUID],
        code = it[CardDatabase.code],
        title = it[CardDatabase.title],
        description = it[CardDatabase.description],
        cardBaseMetadata = it[CardDatabase.metadata],
        status = enumFromTypeValue<CardStatus, Int>(it[CardDatabase.status]),
        createdAt = it[CardDatabase.createdAt],
        modifiedAt = it[CardDatabase.modifiedAt],
    )

    private fun resultRowToColumn(it: ResultRow): Column = Column(
        uuid = it[ColumnDatabase.uuid],
        boardUUID = it[ColumnDatabase.boardUUID],
        code = it[ColumnDatabase.code],
        title = it[ColumnDatabase.title],
        description = it[ColumnDatabase.description],
        status = enumFromTypeValue<ColumnStatus, Int>(it[ColumnDatabase.status]),
        type = enumFromTypeValue<ColumnType, String>(it[ColumnDatabase.type]),
        index = it[ColumnDatabase.index],
        isDefault = it[ColumnDatabase.isDefault],
        createdAt = it[ColumnDatabase.createdAt],
        modifiedAt = it[ColumnDatabase.modifiedAt],
    )

    private fun resultRowToTag(it: ResultRow): Tag = Tag(
        uuid = it[TagDatabase.uuid],
        boardUUID = it[TagDatabase.boardUUID],
        title = it[TagDatabase.title],
        color = it[TagDatabase.color],
        description = it[TagDatabase.description],
        status = enumFromTypeValue<TagStatus, Int>(it[TagDatabase.status]),
        createdAt = it[TagDatabase.createdAt],
        modifiedAt = it[TagDatabase.modifiedAt],
    )

    private fun resultRowToColumnRule(it: ResultRow): ColumnRule = ColumnRule(
        uuid = it[ColumnRuleDatabase.uuid],
        columnUUID = it[ColumnRuleDatabase.columnUUID],
        title = it[ColumnRuleDatabase.title],
        type = enumFromTypeValue<ColumnRuleType, String>(it[ColumnRuleDatabase.type]),
        metadata = it[ColumnRuleDatabase.metadata],
        createdAt = it[ColumnRuleDatabase.createdAt],
        modifiedAt = it[ColumnRuleDatabase.modifiedAt],
    )
}