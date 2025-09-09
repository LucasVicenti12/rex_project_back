package com.delice.crm.modules.order.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.product.infra.database.ProductDatabase
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime

object OrderDatabase : Table("order") {
    var uuid = uuid("uuid").uniqueIndex()
    var code = integer("code").autoIncrement()
    var discount = double("discount")
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
): ExposedFilter<OrderDatabase>{
    override fun toFilter(table: OrderDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["code"]?.let {
            val code = it.toString().toIntOrNull()
            if (code != null) {
                op = op.and(table.code eq code)
            }
        }

        return op
    }
}