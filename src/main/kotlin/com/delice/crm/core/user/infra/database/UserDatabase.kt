package com.delice.crm.core.user.infra.database

import com.delice.crm.core.utils.extensions.removeSpecialChars
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.formatter.DateFormatter
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.concat
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDate

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
}

data class UserFilter(
    val parameters: Map<String, Any?>
) : ExposedFilter<UserDatabase> {
    override fun toFilter(table: UserDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["login"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and((table.login like "%$it%"))
            }
        }

        parameters["userType"]?.let {
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
            if (it is Int && it != 0) {
                op = op.and((table.status eq it))
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

        parameters["dateOfBirth"]?.let {
            if (it is String && it.isNotBlank()) {
                val date = LocalDate.parse(it, DateFormatter)

                op = op.and(table.dateOfBirth eq date)
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