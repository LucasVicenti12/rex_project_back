package com.delice.crm.modules.order.domain.usecase;

import com.delice.crm.modules.order.domain.entities.ManipulateOrder
import com.delice.crm.modules.order.domain.entities.ManipulateOrderItem
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.usecase.response.OrderItemListResponse
import com.delice.crm.modules.order.domain.usecase.response.OrderPaginationResponse
import com.delice.crm.modules.order.domain.usecase.response.OrderResponse
import java.util.*

interface OrderUseCase {
    fun createOrder(order: Order): OrderResponse
    fun changeOrderDefaultDiscount(orderUUID: UUID, manipulateOrder: ManipulateOrder): OrderResponse
    fun addOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): OrderItemListResponse
    fun changeOrderItemDiscount(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): OrderItemListResponse
    fun removeOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): OrderItemListResponse
    fun changeOrderStatus(orderUUID: UUID, manipulateOrder: ManipulateOrder): OrderResponse
    fun getOrderByUUID(orderUUID: UUID): OrderResponse
    fun getPaginatedOrder(count: Int, page: Int, params: Map<String, Any?>): OrderPaginationResponse
}
