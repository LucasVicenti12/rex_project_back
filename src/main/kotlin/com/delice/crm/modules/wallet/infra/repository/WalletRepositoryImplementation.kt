package com.delice.crm.modules.wallet.infra.repository

import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.wallet.domain.entities.Wallet
import com.delice.crm.modules.wallet.domain.entities.WalletStatus
import com.delice.crm.modules.wallet.domain.repository.WalletRepository
import com.delice.crm.modules.wallet.infra.database.WalletCustomersDatabase
import com.delice.crm.modules.wallet.infra.database.WalletDatabase
import com.delice.crm.modules.wallet.infra.database.WalletFilter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Service
class WalletRepositoryImplementation(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository
) : WalletRepository {
    override fun createWallet(wallet: Wallet, userUUID: UUID): Wallet? = transaction {
        val walletUUID = UUID.randomUUID()

        WalletDatabase.insert {
            it[uuid] = walletUUID
            it[label] = wallet.label!!
            it[observation] = wallet.observation
            it[status] = wallet.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
            it[accountable] = wallet.accountable!!.uuid!!
            it[createdBy] = wallet.createdBy!!
            it[modifiedBy] = wallet.modifiedBy!!
        }

        wallet.customers!!.forEach { customer ->
            WalletCustomersDatabase.insert {
                it[uuid] = UUID.randomUUID()
                it[WalletCustomersDatabase.walletUUID] = walletUUID
                it[customerUUID] = customer.uuid!!
            }
        }

        getWalletByUUID(walletUUID)
    }

    override fun updateWallet(wallet: Wallet, userUUID: UUID): Wallet? = transaction {
        WalletDatabase.update({
            WalletDatabase.uuid eq wallet.uuid!!
        }) {
            it[label] = wallet.label!!
            it[observation] = wallet.observation
            it[status] = wallet.status!!.code
            it[modifiedAt] = LocalDateTime.now()
            it[accountable] = wallet.accountable!!.uuid!!
            it[modifiedBy] = wallet.modifiedBy!!
        }

        WalletCustomersDatabase.deleteWhere { walletUUID eq wallet.uuid!! }

        wallet.customers!!.forEach { customer ->
            WalletCustomersDatabase.insert {
                it[uuid] = UUID.randomUUID()
                it[walletUUID] = walletUUID
                it[customerUUID] = customer.uuid!!
            }
        }

        getWalletByUUID(wallet.uuid!!)
    }

    override fun getWalletByUUID(walletUUID: UUID): Wallet? = transaction {
        WalletDatabase.selectAll().where(WalletDatabase.uuid eq walletUUID).map {
            resultRowToWallet(it)
        }.firstOrNull()
    }

    override fun getUserWalletByUUID(userUUID: UUID): Wallet? = transaction {
        WalletDatabase.selectAll().where(WalletDatabase.accountable eq userUUID).map {
            resultRowToWallet(it)
        }.firstOrNull()
    }

    override fun getCustomerWallet(customerUUID: UUID, walletUUID: UUID?): Wallet? = transaction {
        val query = WalletDatabase
            .join(
                otherTable = WalletCustomersDatabase,
                joinType = JoinType.INNER,
                onColumn = WalletCustomersDatabase.customerUUID eq customerUUID,
            )
            .selectAll()

        if (walletUUID != null) {
            query.where(WalletCustomersDatabase.walletUUID neq walletUUID)
        }

        query.map {
            resultRowToWallet(it)
        }.firstOrNull()
    }

    override fun getWalletPagination(count: Int, page: Int, params: Map<String, Any?>): Pagination<Wallet> =
        transaction {
            val query = WalletDatabase
                .selectAll()
                .where(WalletFilter(params).toFilter(WalletDatabase))

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .orderBy(WalletDatabase.modifiedAt)
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    resultRowToWallet(it)
                }

            Pagination(
                items = items,
                page = page,
                total = total
            )
        }

    private fun resultRowToWallet(it: ResultRow): Wallet {
        val wallet = Wallet(
            uuid = it[WalletDatabase.uuid],
            label = it[WalletDatabase.label],
            observation = it[WalletDatabase.observation],
            status = enumFromTypeValue<WalletStatus, Int>(it[WalletDatabase.status]),
            createdAt = it[WalletDatabase.createdAt],
            modifiedAt = it[WalletDatabase.modifiedAt],
            createdBy = it[WalletDatabase.createdBy],
            modifiedBy = it[WalletDatabase.modifiedBy],
        )

        wallet.customers = WalletCustomersDatabase
            .selectAll()
            .where(WalletCustomersDatabase.walletUUID eq wallet.uuid!!)
            .map { customer ->
                customerRepository.getCustomerByUUID(
                    customer[WalletCustomersDatabase.customerUUID]
                )!!
            }

        wallet.accountable = userRepository.getUserByUUID(
            it[WalletDatabase.accountable]
        )

        return wallet
    }
}