package com.delice.crm.modules.dashboard.infra.database

import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.order.infra.database.OrderDatabase
import com.delice.crm.modules.wallet.infra.database.WalletDatabase
import com.delice.crm.modules.wallet.infra.database.WalletCustomersDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class DashboardFilter(
    val parameters: Map<String, Any?>
) : ExposedFilter<OrderDatabase> {

    private fun decodeUrlValue(value: Any?): String? {
        return when (value) {
            is String -> URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
            else -> value?.toString()
        }
    }

    override fun toFilter(table: OrderDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["customerName"]?.let {
            val decodedValue = decodeUrlValue(it)
            if (decodedValue != null && decodedValue.isNotBlank()) {
                val customerName = decodedValue.trim().lowercase()

                val customerUUIDs = CustomerDatabase
                    .select(CustomerDatabase.uuid)
                    .where {
                        (CustomerDatabase.companyName like "%$customerName%") or
                                (CustomerDatabase.tradingName like "%$customerName%") or
                                (CustomerDatabase.personName like "%$customerName%")
                    }
                    .map { it[CustomerDatabase.uuid] }

                if (customerUUIDs.isNotEmpty()) {
                    op = op.and(table.customerUUID inList customerUUIDs)
                } else {
                    op = op.and(Op.FALSE)
                }
            }
        }

        parameters["walletName"]?.let {
            val decodedValue = decodeUrlValue(it)
            if (decodedValue != null && decodedValue.isNotBlank()) {
                val walletName = decodedValue.trim().lowercase()

                val walletUUIDs = WalletDatabase
                    .select(WalletDatabase.uuid)
                    .where { WalletDatabase.label like "%$walletName%" }
                    .map { it[WalletDatabase.uuid] }

                if (walletUUIDs.isNotEmpty()) {
                    val customerUUIDsInWallets = WalletCustomersDatabase
                        .select(WalletCustomersDatabase.customerUUID)
                        .where { WalletCustomersDatabase.walletUUID inList walletUUIDs }
                        .map { it[WalletCustomersDatabase.customerUUID] }

                    if (customerUUIDsInWallets.isNotEmpty()) {
                        op = op.and(table.customerUUID inList customerUUIDsInWallets)
                    } else {
                        op = op.and(Op.FALSE)
                    }
                } else {
                    op = op.and(Op.FALSE)
                }
            }
        }

        parameters["operatorName"]?.let {
            val decodedValue = decodeUrlValue(it)
            if (decodedValue != null && decodedValue.isNotBlank()) {
                val operatorName = decodedValue.trim().lowercase()

                val operatorUUIDs = UserDatabase
                    .select(UserDatabase.uuid)
                    .where {
                        (UserDatabase.name like "%$operatorName%") or
                                (UserDatabase.surname like "%$operatorName%") or
                                (concat(UserDatabase.name, UserDatabase.surname) like "%$operatorName%")
                    }
                    .map { it[UserDatabase.uuid] }

                if (operatorUUIDs.isNotEmpty()) {
                    op = op.and(table.operatorUUID inList operatorUUIDs)
                } else {
                    op = op.and(Op.FALSE)
                }
            }
        }

        parameters["monthYear"]?.let {
            val decodedValue = decodeUrlValue(it)
            if (decodedValue != null && decodedValue.isNotBlank()) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("MM/yyyy")
                    val yearMonth = YearMonth.parse(decodedValue, formatter)

                    val startDate = yearMonth.atDay(1).atStartOfDay()
                    val endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59)

                    op = op.and(table.createdAt greaterEq startDate)
                    op = op.and(table.createdAt lessEq endDate)
                } catch (e: Exception) {
                }
            }
        }

        parameters["startDate"]?.let { startDateParam ->
            parameters["endDate"]?.let { endDateParam ->
                val decodedStartDate = decodeUrlValue(startDateParam)
                val decodedEndDate = decodeUrlValue(endDateParam)

                if (decodedStartDate != null && decodedEndDate != null &&
                    decodedStartDate.isNotBlank() && decodedEndDate.isNotBlank()) {
                    try {
                        val startDate = LocalDateTime.parse("${decodedStartDate}T00:00:00")
                        val endDate = LocalDateTime.parse("${decodedEndDate}T23:59:59")

                        op = op.and(table.createdAt greaterEq startDate)
                        op = op.and(table.createdAt lessEq endDate)
                    } catch (e: Exception) {
                    }
                }
            }
        }

        return op
    }
}