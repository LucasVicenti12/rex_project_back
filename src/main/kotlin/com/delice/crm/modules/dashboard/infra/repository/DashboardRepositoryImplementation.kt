package com.delice.crm.modules.dashboard.infra.repository

import com.delice.crm.core.user.domain.entities.SimplesSalesUser
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankValues
import com.delice.crm.modules.dashboard.domain.repository.DashboardRepository
import com.delice.crm.modules.order.domain.entities.OrderStatus
import com.delice.crm.modules.order.infra.database.OrderDatabase
import com.delice.crm.modules.order.infra.database.OrderItemDatabase
import com.delice.crm.modules.product.domain.entities.SimpleProductWithSales
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.wallet.domain.entities.SimpleWallet
import com.delice.crm.modules.wallet.infra.database.WalletCustomersDatabase
import com.delice.crm.modules.wallet.infra.database.WalletDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class DashboardRepositoryImplementation : DashboardRepository {
    override fun getDashboardCustomer(): DashboardCustomerValues = transaction {
        val statusCounts = CustomerDatabase
            .selectAll()
            .map { it[CustomerDatabase.status] }
            .groupingBy<Int, Int> { it }
            .eachCount()

        DashboardCustomerValues(
            pending = statusCounts[CustomerStatus.PENDING.code] ?: 0,
            inactive = statusCounts[CustomerStatus.INACTIVE.code] ?: 0,
            fit = statusCounts[CustomerStatus.FIT.code] ?: 0,
            notFit = statusCounts[CustomerStatus.NOT_FIT.code] ?: 0
        )
    }

    override fun getDashboardOrder(): DashboardOrderValues = transaction {
        val statusCounts = OrderDatabase
            .selectAll()
            .map { it[OrderDatabase.status] }
            .groupingBy<Int, Int> { it }
            .eachCount()

        DashboardOrderValues(
            open = statusCounts[OrderStatus.OPEN.code] ?: 0,
            closed = statusCounts[OrderStatus.CLOSED.code] ?: 0,
            canceled = statusCounts[OrderStatus.CANCELED.code] ?: 0
        )
    }

    private fun getTopProducts(limit: Int, sortOrder: SortOrder): List<SimpleProductWithSales> {
        val quantitySum = OrderItemDatabase.quantity.sum()

        return OrderDatabase
            .join(OrderItemDatabase, JoinType.INNER) { OrderItemDatabase.orderUUID eq OrderDatabase.uuid }
            .join(ProductDatabase, JoinType.INNER) { ProductDatabase.uuid eq OrderItemDatabase.productUUID }
            .select(
                ProductDatabase.uuid,
                ProductDatabase.name,
                quantitySum
            )
            .where { OrderDatabase.status eq OrderStatus.CLOSED.code }
            .groupBy(ProductDatabase.uuid, ProductDatabase.name)
            .orderBy(quantitySum, sortOrder)
            .limit(limit)
            .map {
                SimpleProductWithSales(
                    uuid = it[ProductDatabase.uuid],
                    name = it[ProductDatabase.name],
                    quantity = it[quantitySum] ?: 0
                )
            }
    }

    override fun getDashboardRank(): DashboardRankValues = transaction {
        DashboardRankValues(
            bestProducts = getTopProducts(5, SortOrder.DESC),
            lessProducts = getTopProducts(5, SortOrder.ASC)
        )
    }

    override fun getDashboardTotalSold(): Double = transaction {
        OrderItemDatabase
            .innerJoin(OrderDatabase) { OrderItemDatabase.orderUUID eq OrderDatabase.uuid }
            .innerJoin(ProductDatabase) { ProductDatabase.uuid eq OrderItemDatabase.productUUID }
            .select(OrderItemDatabase.quantity, ProductDatabase.price)
            .where { OrderDatabase.status eq OrderStatus.CLOSED.code }
            .map {
                val quantity = it[OrderItemDatabase.quantity]
                val price = it[ProductDatabase.price]
                quantity * price
            }
            .sum()
    }

    //esse foi no chat, 5 da manhã to cansado e o sql parou de funcionar. Arrumar mais tarde
    override fun getDashboardMostWalletSold(): SimpleWallet? = transaction {
        WalletDatabase
            .innerJoin(WalletCustomersDatabase) { WalletCustomersDatabase.walletUUID eq WalletDatabase.uuid }
            .innerJoin(CustomerDatabase) { CustomerDatabase.uuid eq WalletCustomersDatabase.customerUUID }
            .innerJoin(OrderDatabase) { OrderDatabase.customerUUID eq CustomerDatabase.uuid }
            .innerJoin(OrderItemDatabase) { OrderItemDatabase.orderUUID eq OrderDatabase.uuid }
            .innerJoin(ProductDatabase) { ProductDatabase.uuid eq OrderItemDatabase.productUUID }
            .select(
                WalletDatabase.uuid,
                WalletDatabase.label,
                OrderItemDatabase.quantity,
                ProductDatabase.price
            )
            .where {
                OrderDatabase.status eq OrderStatus.CLOSED.code
            }
            .map {
                val uuid = it[WalletDatabase.uuid]
                val label = it[WalletDatabase.label]
                val saleValue = it[OrderItemDatabase.quantity] * it[ProductDatabase.price]
                uuid to Pair(label, saleValue)
            }
            .groupBy { it.first }
            .map { (walletUUID, sales) ->
                val walletLabel = sales.first().second.first
                val totalSold = sales.sumOf { it.second.second }
                SimpleWallet(walletUUID, walletLabel, totalSold)
            }
            .sortedByDescending { it.sold }
            .firstOrNull()
    }

    override fun getDashboardMostOperatorSold(): SimplesSalesUser? = transaction {
        OrderDatabase
            .innerJoin(OrderItemDatabase) { OrderItemDatabase.orderUUID eq OrderDatabase.uuid }
            .innerJoin(ProductDatabase) { ProductDatabase.uuid eq OrderItemDatabase.productUUID }
            .innerJoin(UserDatabase) { UserDatabase.uuid eq OrderDatabase.operatorUUID }
            .select(
                UserDatabase.uuid,
                UserDatabase.name,
                UserDatabase.surname,
                OrderItemDatabase.quantity,
                ProductDatabase.price
            )
            .where {
                OrderDatabase.status eq OrderStatus.CLOSED.code
            }
            .map {
                SimplesSalesUser(
                    uuid = it[UserDatabase.uuid],
                    name = "${it[UserDatabase.name]} ${it[UserDatabase.surname]}",
                    sold = it[OrderItemDatabase.quantity] * it[ProductDatabase.price]
                )
            }
            .groupBy { it.uuid }
            .map { (userUUID, userSales) ->
                val firstUser = userSales.first()
                SimplesSalesUser(
                    uuid = userUUID,
                    name = firstUser.name,
                    sold = userSales.sumOf { it.sold }
                )
            }
            .maxByOrNull { it.sold }
    }

}


