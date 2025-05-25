package com.delice.crm.modules.wallet.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.or

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

        parameters["label"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.label like "%$it%")
            }
        }

        parameters["status"]?.let {
            if (it is Int) {
                op = op.and(table.status eq it)
            }
        }

        parameters["accountable"]?.let {
            if (it is String && it.isNotBlank()) {
                val userUUID = UserDatabase
                    .select(UserDatabase.uuid)
                    .where {
                        (UserDatabase.name like "%$it%") or (UserDatabase.surname like "%$it") or (UserDatabase.login like "%$it")
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

        return op
    }
}