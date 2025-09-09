package com.delice.crm.modules.order.domain.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.order.domain.entities.ManipulateOrder
import com.delice.crm.modules.order.domain.entities.ManipulateOrderItem
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.entities.OrderItem
import java.util.UUID

interface OrderRepository {
    fun createOrder(order: Order): Order?
    fun changeOrderDefaultDiscount(orderUUID: UUID, manipulateOrder: ManipulateOrder): Order?
    fun addOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): List<OrderItem>?
    fun changeOrderItemDiscount(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): List<OrderItem>?
    fun removeOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): List<OrderItem>?
    fun getOrderItemByProductUUID(orderUUID: UUID, productUUID: UUID): OrderItem?
    fun getOrderItemsByUUID(orderUUID: UUID): List<OrderItem>?
    fun changeOrderStatus(orderUUID: UUID, manipulateOrder: ManipulateOrder): Order?
    fun getOrderByUUID(orderUUID: UUID): Order?
    fun getPaginatedOrder(count: Int, page: Int, params: Map<String, Any?>): Pagination<Order>?
}