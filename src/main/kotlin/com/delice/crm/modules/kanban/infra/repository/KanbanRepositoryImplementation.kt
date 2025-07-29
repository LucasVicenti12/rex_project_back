package com.delice.crm.modules.kanban.infra.repository

import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.entities.Column
import com.delice.crm.modules.kanban.domain.entities.ColumnType
import com.delice.crm.modules.kanban.domain.repository.KanbanRepository
import com.delice.crm.modules.kanban.infra.database.*
import com.delice.crm.modules.wallet.domain.repository.WalletRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Service
class KanbanRepositoryImplementation(
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository
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

            if (card.metadata != null) {
                it[metadata] = card.metadata
            }
        }

        return@transaction getCardByUUID(uuid)
    }

    override fun registerColumn(column: Column): Column? = transaction {
        val uuid = UUID.randomUUID()

        ColumnDatabase.insert {
            it[ColumnDatabase.uuid] = uuid
            it[code] = column.code!!
            it[boardUUID] = column.boardUUID!!
            it[title] = column.title!!
            it[description] = column.description
            it[status] = column.status!!.code
            it[index] = column.index!!
            it[type] = column.type!!.type
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

            if (card.metadata != null) {
                it[metadata] = card.metadata
            }
        }

        return@transaction getCardByUUID(card.uuid!!)
    }

    override fun updateColumn(column: Column): Column? = transaction {
        ColumnDatabase.update({ ColumnDatabase.uuid eq column.uuid!! }) {
            it[title] = column.title!!
            it[description] = column.description
            it[status] = column.status!!.code
            it[index] = column.index!!
            it[type] = column.type!!.type
            it[modifiedAt] = LocalDateTime.now()
        }

        saveAllowedColumns(column.uuid!!, column.allowedColumns!!)

        return@transaction getColumnByUUID(column.uuid!!)
    }

    override fun updateTag(tag: Tag): Tag? = transaction {
        TagDatabase.update({ TagDatabase.uuid eq tag.uuid!! }) {
            it[color] = tag.color!!
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
            resultRowToColumn(it)
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
        val cards = CardDatabase.selectAll().where { CardDatabase.boardUUID eq uuid }.map {
            val card = resultRowToCard(it)

            if (card.metadata != null) {
                if (card.metadata!!.customer != null) {
                    val customer = customerRepository.getCustomerByUUID(
                        UUID.fromString(card.metadata!!.customer!!.uuid!!)
                    )

                    if (customer != null) {
                        card.movable = when (customer.status) {
                            CustomerStatus.FIT -> false
                            CustomerStatus.PENDING -> true
                            else -> false
                        }

                        card.metadata!!.customer!!.companyName = customer.companyName
                    }
                }

                if (card.metadata!!.wallet != null) {
                    val wallet = walletRepository.getWalletByUUID(
                        UUID.fromString(card.metadata!!.wallet!!.uuid!!)
                    )

                    if (wallet != null) {
                        card.metadata!!.wallet!!.label = wallet.label
                    }
                }
            }

            card
        }

        return@transaction cards
    }

    override fun getColumnsByBoardUUID(uuid: UUID): List<Column>? = transaction {
        val column = ColumnDatabase.selectAll().where { ColumnDatabase.boardUUID eq uuid }.map {
            resultRowToColumn(it)
        }

        return@transaction column
    }

    override fun getTagsByBoardUUID(uuid: UUID): List<Tag>? = transaction {
        val tag = TagDatabase.selectAll().where { TagDatabase.boardUUID eq uuid }.map {
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

    private fun saveAllowedColumns(columnUUID: UUID, allowed: List<UUID>) {
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
        }
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
        metadata = it[CardDatabase.metadata],
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
        createdAt = it[ColumnDatabase.createdAt],
        modifiedAt = it[ColumnDatabase.modifiedAt],
    )

    private fun resultRowToTag(it: ResultRow): Tag = Tag(
        uuid = it[TagDatabase.uuid],
        boardUUID = it[TagDatabase.boardUUID],
        color = it[TagDatabase.color],
        description = it[TagDatabase.description],
        status = enumFromTypeValue<TagStatus, Int>(it[TagDatabase.status]),
        createdAt = it[TagDatabase.createdAt],
        modifiedAt = it[TagDatabase.modifiedAt],
    )
}