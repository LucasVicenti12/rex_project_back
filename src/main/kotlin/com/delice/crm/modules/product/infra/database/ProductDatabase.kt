package com.delice.crm.modules.product.infra.database

import com.delice.crm.core.utils.filter.ExposedFilter
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.or

object ProductDatabase : Table("product") {
    val uuid = uuid("uuid").uniqueIndex()
    val code = varchar("code", 30).uniqueIndex()
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
                val value = it.trim()

                // Tenta converter para número quando for possível (pra peso e preço)
                val numericValue = value.toDoubleOrNull()
                val statusValue = if (value == "ativo") 0 else if (value == "inativo") 1 else null

                val generalFilter = Op.build {
                    // Campos de texto
                    (table.code like "%$value%") or
                            (table.name like "%$value%") or
                            (table.description like "%$value%") or

                            // Campos numéricos
                            (if (numericValue != null) (table.weight eq numericValue) else Op.FALSE) or
                            (if (numericValue != null) (table.price eq numericValue) else Op.FALSE) or

                            // Status
                            (if (statusValue != null) (table.status eq statusValue) else Op.FALSE)
                }

                op = op.and(generalFilter)
            }
        }


        parameters["code"]?.let {
            if (it is String && it.isNotBlank()) {
                op = op.and(table.code like "%$it%")
            }
        }

        parameters["name"]?.let {
            if (it is String && it.isNotBlank()) {
                println(it)
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