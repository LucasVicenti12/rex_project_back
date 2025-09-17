package com.delice.crm.modules.order.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.entities.OrderItem
import com.delice.crm.modules.order.domain.exceptions.OrderExceptions

class OrderResponse(
    val order: Order? = null,
    val error: OrderExceptions? = null,
)

class OrderPaginationResponse(
    val orders: Pagination<Order>? = null,
    val error: OrderExceptions? = null,
)