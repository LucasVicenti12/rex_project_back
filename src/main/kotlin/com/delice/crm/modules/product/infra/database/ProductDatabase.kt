package com.delice.crm.modules.product.infra.database

import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.core.utils.filter.ExposedOrderBy
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.ordernation.SortBy
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.or
import java.security.URIParameter

object ProductDatabase : Table("product") {
    val uuid = uuid("uuid").uniqueIndex()
    val code = integer("code").uniqueIndex()
    val name = varchar("name", 60)
    val description = text("description")
    val price = double("price")
    val weight = double("weight")
    val status = integer("status")
    val createdAt = date("created_at")
    val modifiedAt = date("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_product_uuid")
}

object ProductMediaDatabase : Table("product_media") {
    val uuid = uuid("uuid").uniqueIndex()
    val productUUID = uuid("product_uuid") references ProductDatabase.uuid
    val image = blob("image")
    val isPrincipal = bool("is_principal")
    val createdAt = date("created_at")
    val modifiedAt = date("modified_at")
}

data class ProductFilter(
    val parameters: Map<String, Any?>,
) : ExposedFilter<ProductDatabase> {
    override fun toFilter(table: ProductDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["allFields"]?.let {
            if (it is String && it.isNotBlank()) {
                val value = it.trim().lowercase()

                val numericDoubleValue = value.toDoubleOrNull()
                val numericIntegerValue = value.toIntOrNull()

                val generalFilter = Op.build {
                    (table.name like "%$value%") or
                    (if (numericDoubleValue != null) (table.weight eq numericDoubleValue) else Op.FALSE) or
                    (if (numericDoubleValue != null) (table.price eq numericDoubleValue) else Op.FALSE) or
                    (if (numericIntegerValue != null) (table.code eq numericIntegerValue) else Op.FALSE)
                }

                op = op.and(generalFilter)
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

        parameters["code"]?.let {
            val code = it.toString().toIntOrNull()
            if (code != null) {
                op = op.and(table.code eq code)
            }
        }

        parameters["name"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.name like "%$it%")
            }
        }

        parameters["weight"]?.let {
            val weightValue = it.toString().toDoubleOrNull()
            if (weightValue != null) {
                op = op.and(table.weight eq weightValue)
            }
        }

        parameters["price"]?.let {
            val priceValue = it.toString().toDoubleOrNull()
            if (priceValue != null) {
                op = op.and(table.price eq priceValue)
            }
        }

        return op
    }
}

data class ProductOrderBy(
    private val orderBy: OrderBy? = null,
) : ExposedOrderBy<ProductDatabase> {
    override fun toOrderBy(): Pair<Expression<*>, SortOrder> {
        if (orderBy == null) {
            return ProductDatabase.name to SortOrder.ASC
        }

        val orderByMap = mapOf(
            "uuid" to ProductDatabase.uuid,
            "code" to ProductDatabase.code,
            "name" to ProductDatabase.name,
            "price" to ProductDatabase.price,
            "weight" to ProductDatabase.weight,
            "createdAt" to ProductDatabase.createdAt,
            "modifiedAt" to ProductDatabase.modifiedAt,
            "status" to ProductDatabase.status
        )

        val sortByMap = mapOf(
            SortBy.ASC to SortOrder.ASC,
            SortBy.DESC to SortOrder.DESC,
        )

        val column = orderByMap[orderBy.orderBy]
        val sort = sortByMap[orderBy.sortBy]

        if (column == null || sort == null) {
            return ProductDatabase.name to SortOrder.ASC
        }

        return column to sort
    }
}