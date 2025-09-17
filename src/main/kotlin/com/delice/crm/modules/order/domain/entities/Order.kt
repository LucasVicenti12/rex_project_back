package com.delice.crm.modules.order.domain.entities

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.product.domain.entities.Product
import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime
import java.util.UUID

class Order(
    val uuid: UUID? = null,
    val code: Int? = null,
    var grossPrice: Double? = null,
    var netPrice: Double? = null,
    var discount: Double? = null,
    var totalItems: Int? = 0,
    var totalProducts: Int? = 0,
    var weight: Double? = 0.0,
    val defaultDiscount: Double? = null,
    var items: List<OrderItem>? = emptyList(),
    val customer: Customer? = null,
    val status: OrderStatus? = OrderStatus.OPEN,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
    var operator: User? = null,
)

class OrderTotals(
    var grossPrice: Double,
    var netPrice: Double,
    var discount: Double,
    var totalItems: Int,
    var totalProducts: Int,
    var weight: Double,
)

class OrderItem (
    val quantity: Int = 0,
    var grossPrice: Double = 0.0,
    var netPrice: Double = 0.0,
    var weight: Double = 0.0,
    val discount: Double = 0.0,
    val product: Product? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

class OrderItemTotals(
    var grossPrice: Double,
    var netPrice: Double,
    var weight: Double,
)

enum class OrderStatus(override val code: Int) : HasCode{
    OPEN(0),
    CLOSED(1),
    CANCELED(2)
}

data class ManipulateOrderItem(
    val products: List<UUID>,
    val quantity: Int = 0,
    val discount: Double = 0.0,
)

data class ManipulateOrder(
    val discount: Double = 0.0,
    val status: OrderStatus = OrderStatus.OPEN,
)