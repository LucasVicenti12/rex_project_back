package com.delice.crm.modules.wallet.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.or
import java.time.format.DateTimeFormatter

object WalletDatabase : Table("wallet") {
    val uuid = uuid("uuid").uniqueIndex()
    val label = varchar("label", 60)
    val observation = text("observation").nullable()
    val status = integer("status")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")
    var accountable = uuid("accountable") references UserDatabase.uuid
    var createdBy = uuid("created_by") references UserDatabase.uuid
    var modifiedBy = uuid("modified_by") references UserDatabase.uuid

    override val primaryKey: PrimaryKey = PrimaryKey(uuid, name = "pk_wallet")
}

object WalletCustomersDatabase : Table("wallet_customers") {
    val uuid = uuid("uuid").uniqueIndex()
    val walletUUID = uuid("wallet_uuid") references WalletDatabase.uuid
    val customerUUID = uuid("customer_uuid") references CustomerDatabase.uuid
}

data class WalletFilter(
    val parameters: Map<String, Any?>
) : ExposedFilter<WalletDatabase> {
    override fun toFilter(table: WalletDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.lowercase()

                val numericIntValue = value.toIntOrNull()

                // procurar usuários correspondentes
                val matchingUserUUIDs = UserDatabase
                    .select(UserDatabase.uuid)
                    .where {
                        (UserDatabase.name like "%$value%") or
                                (UserDatabase.surname like "%$value%") or
                                (UserDatabase.login like "%$value%")
                    }.map { r -> r[UserDatabase.uuid] }

                val generalFilter = Op.build {
                    (table.label like "%$value%") or
                            (if (numericIntValue != null) (table.status eq numericIntValue) else Op.FALSE) or
                            (if (matchingUserUUIDs.isNotEmpty()) (table.accountable inList matchingUserUUIDs) else Op.FALSE)
                }

                op = op.and(generalFilter)
            }
        }

        parameters["label"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.label like "%$it%")
            }
        }

        parameters["status"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()

                val statusValue = if (value == "active") 0 else if (value == "inactive") 1 else null

                if (statusValue != null) {
                    op = op.and(table.status eq statusValue)
                } else {
                    op = op.and(Op.FALSE)
                }
            }
        }

        parameters["accountable"]?.let {
            if (it is String && it.isNotBlank()) {
                val userUUID = UserDatabase
                    .select(UserDatabase.uuid)
                    .where {
                        (UserDatabase.name like "%$it%") or (UserDatabase.surname like "%$it%") or (UserDatabase.login like "%$it%")
                    }.map { r ->
                        r[UserDatabase.uuid]
                    }

                if (userUUID.isNotEmpty()) {
                    op = op.and(
                        table.accountable inList userUUID
                    )
                }
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
                        table.createdAt.between(startOfDay, endOfDay)
                    )
                } catch (e: Exception) {
                    throw e
                }
            }
        }

        return op
    }
}

data class WalletOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<WalletDatabase> {

    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) return WalletDatabase.label to SortOrder.ASC

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val sort = sortByMap[orderBy.sortBy] ?: SortOrder.ASC
        
        val column = when (orderBy.orderBy) {
            "uuid" -> WalletDatabase.uuid
            "label" -> WalletDatabase.label
            "status" -> WalletDatabase.status
            "created_at" -> WalletDatabase.createdAt
            "modified_at" -> WalletDatabase.modifiedAt
            "accountable" -> WalletDatabase.accountable
            "created_by" -> WalletDatabase.createdBy
            "modified_by" -> WalletDatabase.modifiedBy

            "customers_quantity" -> object : Expression<Int>() {
                override fun toQueryBuilder(queryBuilder: org.jetbrains.exposed.sql.QueryBuilder) {
                    queryBuilder.append("(SELECT COUNT(wc.customer_uuid) FROM wallet_customers wc WHERE wc.wallet_uuid = wallet.uuid)")
                }
            }

            else -> WalletDatabase.label
        }

        return column to sort
    }
}