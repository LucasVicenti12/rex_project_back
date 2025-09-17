package com.delice.crm.modules.order.domain.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.order.domain.entities.ManipulateOrder
import com.delice.crm.modules.order.domain.entities.ManipulateOrderItem
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.entities.OrderItem
import java.util.UUID

interface OrderRepository {
    fun createOrder(order: Order): Order?
    fun saveOrder(orderUUID: UUID, manipulateOrder: ManipulateOrder): Order?
    fun saveOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): Order?
    fun removeOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): Order?
    fun getOrderItemByProductUUID(orderUUID: UUID, productUUID: UUID): OrderItem?
    fun getOrderItemsByUUID(orderUUID: UUID): List<OrderItem>?
    fun getOrderByUUID(orderUUID: UUID): Order?
    fun getPaginatedOrder(count: Int, page: Int, params: Map<String, Any?>): Pagination<Order>?
}