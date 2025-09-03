package com.delice.crm.modules.customer.infra.database


import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.api.economicActivities.infra.database.EconomicActivityDatabase
import com.delice.crm.core.utils.extensions.removeSpecialChars
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import com.delice.crm.modules.kanban.infra.database.CardDatabase
import com.delice.crm.modules.product.infra.database.ProductDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and

object CustomerDatabase : Table("customer") {
    var uuid = uuid("uuid").uniqueIndex()
    var companyName = varchar("company_name", 90)
    var tradingName = varchar("trading_name", 90)
    var personName = varchar("person_name", 60)
    val document = varchar("document", 14).uniqueIndex()
    var state = char("state", 2)
    var city = varchar("city", 60)
    var address = varchar("address", 150)
    var zipCode = varchar("zip_code", 8)
    var complement = varchar("complement", 60)
    var addressNumber = integer("address_number")
    var observation = text("observation").nullable()
    var status = integer("status")
    var kanbanCardUUID = uuid("kanban_card_uuid").nullable()
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")
    var createdBy = uuid("created_by") references UserDatabase.uuid
    var modifiedBy = uuid("modified_by") references UserDatabase.uuid

    override val primaryKey = PrimaryKey(uuid, name = "pk_customer")

    init {
        foreignKey(
            kanbanCardUUID to CardDatabase.uuid,
            onDelete = ReferenceOption.SET_NULL
        )
    }
}

object CustomerEconomicActivitiesDatabase : Table("customer_economic_activities") {
    var uuid = uuid("uuid").uniqueIndex()
    var customerUUID = uuid("customer_uuid") references CustomerDatabase.uuid
    var economicActivityUUID = uuid("economic_activity_uuid") references EconomicActivityDatabase.uuid
}

object CustomerContactsDatabase : Table("customer_contacts") {
    var uuid = uuid("uuid").uniqueIndex()
    var contactType = varchar("contact_type", 10)
    var label = text("label")
    var isPrincipal = bool("is_principal")
    var customerUUID = uuid("customer_uuid") references CustomerDatabase.uuid
}

data class CustomerFilter(
    val parameters: Map<String, Any?>
) : ExposedFilter<CustomerDatabase> {
    override fun toFilter(table: CustomerDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["companyName"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.companyName like stringLiteral("%$it%"))
            }
        }

        parameters["tradingName"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.tradingName like "%$it%")
            }
        }

        parameters["personName"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.personName like "%$it%")
            }
        }

        parameters["document"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.document like "%${it.removeSpecialChars()}%")
            }
        }

        parameters["state"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.state like "%$it%")
            }
        }

        parameters["address"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.address like "%$it%")
            }
        }

        parameters["city"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.city like "%$it%")
            }
        }

        parameters["zipCode"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.zipCode like "%${it.removeSpecialChars()}%")
            }
        }

        parameters["complement"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.complement like "%$it%")
            }
        }

        parameters["status"]?.let {
            val status = it.toString().toIntOrNull()
            if (status != null) {
                op = op.and(table.status eq status)
            }
        }

        return op
    }
}

data class CustomerOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<CustomerDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) {
            return CustomerDatabase.companyName to SortOrder.ASC
        }

        val orderByMap = mapOf(
            "uuid" to CustomerDatabase.uuid,
            "company_name" to CustomerDatabase.companyName,
            "trading_name" to CustomerDatabase.tradingName,
            "person_name" to CustomerDatabase.personName,
            "status" to CustomerDatabase.status,
            "document" to CustomerDatabase.document,
            "state" to CustomerDatabase.state,
            "city" to CustomerDatabase.city,
            "address" to CustomerDatabase.address,
            "zip_code" to CustomerDatabase.zipCode,
            "complement" to CustomerDatabase.complement,
            "address_number" to CustomerDatabase.addressNumber,
            "observation" to CustomerDatabase.observation,
            "created_at" to CustomerDatabase.createdAt,
            "modified_at" to CustomerDatabase.modifiedAt,
            "created_by" to CustomerDatabase.createdBy,
            "modified_by" to CustomerDatabase.modifiedBy,
        )

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val column = orderByMap[orderBy.orderBy]
        val sort = sortByMap[orderBy.sortBy]

        if (column == null || sort == null) {
            return CustomerDatabase.companyName to SortOrder.ASC
        }

        return column to sort
    }
}