package com.delice.crm.modules.order.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.product.infra.database.ProductMediaDatabase
import com.delice.crm.modules.wallet.infra.database.WalletDatabase
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import java.time.format.DateTimeFormatter

object OrderDatabase : Table("order") {
    var uuid = uuid("uuid").uniqueIndex()
    var code = integer("code").autoIncrement()
    var defaultDiscount = double("default_discount")
    var customerUUID = uuid("customer_uuid") references CustomerDatabase.uuid
    var status = integer("status")
    val createdAt = datetime("created_at")
    val modifiedAt = datetime("modified_at")
    var operatorUUID = uuid("operator_uuid") references UserDatabase.uuid

    override val primaryKey: PrimaryKey = PrimaryKey(code, name = "pk_order")
}

object OrderItemDatabase : Table("order_item") {
    val orderUUID = uuid("order_uuid") references OrderDatabase.uuid
    val quantity = integer("quantity")
    val discount = double("discount")
    val productUUID = uuid("product_uuid") references ProductDatabase.uuid
    val createdAt = datetime("created_at")
    val modifiedAt = datetime("modified_at")
}

data class OrderFilter(
    val parameters: Map<String, Any?>,
) : ExposedFilter<OrderDatabase> {
    override fun toFilter(table: OrderDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()
                val numericValue = value.toIntOrNull()
                val numericDoubleValue = value.toDoubleOrNull()

                val customerUUIDsByTradingName = CustomerDatabase
                    .select(CustomerDatabase.uuid)
                    .where { CustomerDatabase.tradingName.lowerCase() like "%$value%" }
                    .map { r -> r[CustomerDatabase.uuid] }

                val customerUUIDsByCompanyName = CustomerDatabase
                    .select(CustomerDatabase.uuid)
                    .where { CustomerDatabase.companyName.lowerCase() like "%$value%" }
                    .map { r -> r[CustomerDatabase.uuid] }

                val generalFilter = Op.build {

                    (numericValue?.let { n -> table.code eq n } ?: Op.FALSE) or
                            (numericDoubleValue?.let { nd -> table.defaultDiscount eq nd } ?: Op.FALSE) or

                            (if (customerUUIDsByTradingName.isNotEmpty())
                                table.customerUUID inList customerUUIDsByTradingName else Op.FALSE) or

                            (if (customerUUIDsByCompanyName.isNotEmpty())
                                table.customerUUID inList customerUUIDsByCompanyName else Op.FALSE)
                }

                op = op.and(generalFilter)
            }
        }

        parameters["trading_name"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()

                val customerUUID = CustomerDatabase
                    .select(CustomerDatabase.uuid)
                    .where {
                        CustomerDatabase.tradingName.lowerCase() like "%$value%"
                    }.map { r ->
                        r[CustomerDatabase.uuid]
                    }

                if (customerUUID.isNotEmpty()) {
                    op = op.and(table.customerUUID inList customerUUID)
                }
            }
        }

        parameters["company_name"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()

                val customerUUID = CustomerDatabase
                    .select(CustomerDatabase.uuid)
                    .where {
                        CustomerDatabase.companyName.lowerCase() like "%$value%"
                    }.map { r ->
                        r[CustomerDatabase.uuid]
                    }

                if (customerUUID.isNotEmpty()) {
                    op = op.and(table.customerUUID inList customerUUID)
                }
            }
        }

        parameters["code"]?.let {
            it.toString().toIntOrNull()?.let { code ->
                op = op.and(table.code eq code)
            }
        }

        parameters["customerUUID"]?.let {
            runCatching { java.util.UUID.fromString(it.toString()) }.getOrNull()?.let { uuid ->
                op = op.and(table.customerUUID eq uuid)
            }
        }

        parameters["operatorUUID"]?.let {
            runCatching { java.util.UUID.fromString(it.toString()) }.getOrNull()?.let { uuid ->
                op = op.and(table.operatorUUID eq uuid)
            }
        }

        parameters["status"]?.let {
            it.toString().toIntOrNull()?.let { status ->
                op = op.and(table.status eq status)
            }
        }

        parameters["defaultDiscount"]?.let {
            it.toString().toDoubleOrNull()?.let { discount ->
                op = op.and(table.defaultDiscount eq discount)
            }
        }

        parameters["created_at"]?.let {
            if (it is String && it.isNotBlank()) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = java.time.LocalDate.parse(it, formatter)

                    val startOfDay = date.atStartOfDay()
                    val endOfDay = date.atTime(23, 59, 59)

                    op = op.and(
                        op = table.createdAt.between(startOfDay, endOfDay)
                    )
                } catch (e: Exception) {
                    throw e
                }
            }
        }

        return op
    }
}

data class OrderOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<OrderDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) return WalletDatabase.label to SortOrder.ASC

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val sort = sortByMap[orderBy.sortBy] ?: SortOrder.ASC

        val column = when (orderBy.orderBy) {
            "code" -> OrderDatabase.code
            "status" -> OrderDatabase.status
            "created_at" -> OrderDatabase.createdAt
            "modified_at" -> OrderDatabase.modifiedAt
            "customer_trading_name" -> CustomerDatabase.tradingName
            "customer_company_name" -> CustomerDatabase.companyName
            "discount" -> OrderDatabase.defaultDiscount

            "total_items" -> object : Expression<Int>() {
                override fun toQueryBuilder(queryBuilder: org.jetbrains.exposed.sql.QueryBuilder) {
                    queryBuilder.append("(SELECT COUNT(oi.product_uuid) FROM order_item oi WHERE oi.order_uuid = order.uuid)")
                }
            }

//            "gross_price" -> object : Expression<Int>() {
//                override fun toQueryBuilder(queryBuilder: org.jetbrains.exposed.sql.QueryBuilder) {
//                    queryBuilder.append("(SELECT SUM(oi.product_uuid) FROM order_item oi WHERE oi.order_uuid = order.uuid)")
//                }
//            }

            else -> OrderDatabase.code
        }

        return column to sort
    }
}