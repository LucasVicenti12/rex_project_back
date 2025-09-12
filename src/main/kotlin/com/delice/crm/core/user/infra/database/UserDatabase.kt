package com.delice.crm.core.user.infra.database

import com.delice.crm.core.utils.extensions.removeSpecialChars
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.concat
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.or

object UserDatabase : Table("users") {
    var uuid = uuid("uuid").uniqueIndex()
    var login = varchar("login", 60).uniqueIndex()
    var password = text("password")
    var userType = text("user_type")
    var email = varchar("email", 90).nullable()
    var avatar = blob("avatar_image").nullable()
    var status = integer("status")
    var name = varchar("name", 20)
    var surname = varchar("surname", 50)
    var document = varchar("document", 11).uniqueIndex()
    var phone = varchar("phone", 20).nullable()
    var dateOfBirth = date("date_of_birth")
    var state = char("state", 2)
    var city = varchar("city", 60)
    var address = varchar("address", 150)
    var zipCode = varchar("zip_code", 8).nullable()
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_customer")
}

data class UserFilter(
    val parameters: Map<String, Any?>
) : ExposedFilter<UserDatabase> {
    override fun toFilter(table: UserDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()
                val numericValue = value.toIntOrNull()

                val generalFilter = Op.build {
                    (table.login like "%$value%") or
                            (table.userType like "%$value%") or
                            (table.email like "%$value%") or
                            (table.name like "%$value%") or
                            (table.surname like "%$value%") or
                            (concat(table.name, table.surname) like "%$value%") or
                            (table.document like "%${value.removeSpecialChars()}%") or
                            (table.phone like "%${value.removeSpecialChars()}%") or
                            (table.state like "%$value%") or
                            (table.city like "%$value%") or
                            (table.address like "%$value%") or
                            (table.zipCode like "%${value.removeSpecialChars()}%") or
                            (if (numericValue != null) (table.status eq numericValue) else Op.FALSE)
                }
                op = op.and(generalFilter)
            }
        }

        parameters["login"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and((table.login like "%$it%"))
            }
        }

        parameters["user_type"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and((table.userType eq it))
            }
        }

        parameters["email"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and((table.email like "%$it%"))
            }
        }

        parameters["status"]?.let {
            val status = it.toString().toIntOrNull()
            if (status != null) {
                op = op.and(table.status eq status)
            }
        }

        parameters["name"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and((concat(table.name, table.surname) like "%$it%"))
            }
        }

        parameters["document"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and((table.document like "%${it.removeSpecialChars()}%"))
            }
        }

        parameters["phone"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.phone like "%${it.removeSpecialChars()}%")
            }
        }

        parameters["state"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.state like "%$it%")
            }
        }

        parameters["city"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.city like "%$it%")
            }
        }

        return op
    }
}

data class UserOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<UserDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) {
            return UserDatabase.login to SortOrder.ASC
        }

        val orderByMap = mapOf(
            "uuid" to UserDatabase.uuid,
            "login" to UserDatabase.login,
            "user_type" to UserDatabase.userType,
            "email" to UserDatabase.email,
            "status" to UserDatabase.status,
            "name" to concat(UserDatabase.name, UserDatabase.surname),
            "document" to UserDatabase.document,
            "phone" to UserDatabase.phone,
            "date_of_birth" to UserDatabase.dateOfBirth,
            "state" to UserDatabase.state,
            "city" to UserDatabase.city,
            "address" to UserDatabase.address,
            "zip_code" to UserDatabase.zipCode,
            "created_at" to UserDatabase.createdAt,
            "modified_at" to UserDatabase.modifiedAt,
        )

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val column = orderByMap[orderBy.orderBy]
        val sort = sortByMap[orderBy.sortBy]

        if (column == null || sort == null) {
           return UserDatabase.login to SortOrder.ASC
        }

        return column to sort
    }
}