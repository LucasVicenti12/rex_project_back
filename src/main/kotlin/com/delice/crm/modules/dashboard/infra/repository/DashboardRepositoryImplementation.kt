package com.delice.crm.modules.dashboard.infra.repository

import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankValues
import com.delice.crm.modules.dashboard.domain.repository.DashboardRepository
import com.delice.crm.modules.order.domain.entities.OrderStatus
import com.delice.crm.modules.order.infra.database.OrderDatabase
import com.delice.crm.modules.order.infra.database.OrderItemDatabase
import com.delice.crm.modules.product.domain.entities.ProductStatus
import com.delice.crm.modules.product.domain.entities.SimpleProduct
import com.delice.crm.modules.product.domain.entities.SimpleProductWithSales
import com.delice.crm.modules.product.infra.database.ProductDatabase
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.selectAll
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

    fun getDashboardRankValues(): DashboardRankValues = transaction {
        // Alternativa usando a sintaxe de join mais explícita
        val salesByProduct = OrderItemDatabase
            .join(OrderDatabase, JoinType.INNER, additionalConstraint = {
                OrderItemDatabase.orderUUID eq OrderDatabase.uuid
            })
            .select {
                OrderDatabase.status eq OrderStatus.CLOSED.code
            }
            .groupBy(OrderItemDatabase.productUUID)
            .map { row ->
                row[OrderItemDatabase.productUUID] to (row[OrderItemDatabase.quantity.sum()] ?: 0)
            }
            .toMap()

        // Resto do código permanece igual...
        val products = ProductDatabase
            .select {
                ProductDatabase.status eq ProductStatus.ACTIVE.code
            }
            .map { row ->
                val productUUID = row[ProductDatabase.uuid]
                SimpleProductWithSales(
                    uuid = productUUID,
                    name = row[ProductDatabase.name],
                    quantity = salesByProduct[productUUID] ?: 0
                )
            }
            .sortedByDescending { it.quantity }

        DashboardRankValues(
            bestProducts = products.take(5).map { SimpleProduct(it.uuid, it.name) },
            lessProducts = products.takeLast(5).map { SimpleProduct(it.uuid, it.name) }
        )
    }
}


