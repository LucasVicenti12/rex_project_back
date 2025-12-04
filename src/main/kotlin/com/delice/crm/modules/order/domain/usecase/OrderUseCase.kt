package com.delice.crm.modules.order.domain.usecase;

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.order.domain.entities.ManipulateOrder
import com.delice.crm.modules.order.domain.entities.ManipulateOrderItem
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.usecase.response.OrderPaginationResponse
import com.delice.crm.modules.order.domain.usecase.response.OrderResponse
import java.util.*

interface OrderUseCase {
    fun createOrder(order: Order): OrderResponse
    fun saveOrder(orderUUID: UUID, manipulateOrder: ManipulateOrder): OrderResponse
    fun saveOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): OrderResponse
    fun removeOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): OrderResponse
    fun getOrderByUUID(orderUUID: UUID): OrderResponse
    fun getPaginatedOrder(count: Int, page: Int, orderBy: OrderBy?, params: Map<String, Any?>): OrderPaginationResponse
}
