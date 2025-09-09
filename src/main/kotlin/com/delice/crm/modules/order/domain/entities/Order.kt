package com.delice.crm.modules.order.domain.entities

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.product.domain.entities.Product
import java.time.LocalDateTime
import java.util.UUID

class Order(
    val uuid: UUID? = null,
    val code: Int? = null,
    val grossPrice: Double? = null,
    val netPrice: Double? = null,
    val discount: Double? = null,
    val defaultDiscount: Double? = null,
    val items: List<OrderItem>? = emptyList(),
    val customer: Customer? = null,
    val status: OrderStatus? = OrderStatus.OPEN,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
    var operator: User? = null,
)

class OrderItem (
    val quantity: Int? = null,
    val grossPrice: Double? = null,
    val netPrice: Double? = null,
    val discount: Double? = null,
    val product: Product? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

enum class OrderStatus(override val code: Int) : HasCode{
    OPEN(0),
    CLOSED(1),
    CANCELED(2),
}

class ManipulateOrderItem(
    val productUUID: UUID,
    val quantity: Int = 0,
    val discount: Double = 0.0,
)

class ManipulateOrder(
    val discount: Double = 0.0,
    val status: OrderStatus = OrderStatus.OPEN,
)