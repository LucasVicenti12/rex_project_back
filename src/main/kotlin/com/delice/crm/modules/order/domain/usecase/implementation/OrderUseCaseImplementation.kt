package com.delice.crm.modules.order.domain.usecase.implementation

import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.order.domain.entities.ManipulateOrder
import com.delice.crm.modules.order.domain.entities.ManipulateOrderItem
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.entities.OrderStatus
import com.delice.crm.modules.order.domain.exceptions.*
import com.delice.crm.modules.order.domain.repository.OrderRepository
import com.delice.crm.modules.order.domain.usecase.OrderUseCase
import com.delice.crm.modules.order.domain.usecase.response.OrderItemListResponse
import com.delice.crm.modules.order.domain.usecase.response.OrderPaginationResponse
import com.delice.crm.modules.order.domain.usecase.response.OrderResponse
import com.delice.crm.modules.product.domain.entities.ProductStatus
import com.delice.crm.modules.product.domain.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderUseCaseImplementation(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository,
) : OrderUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(OrderUseCaseImplementation::class.java)
    }

    override fun createOrder(order: Order): OrderResponse = try {
        validateOrder(order).let { validate ->
            if (validate != null) {
                OrderResponse(
                    error = validate
                )
            } else {
                val systemUser = getCurrentUser()
                val user = systemUser.getUserData()

                order.operator = user

                OrderResponse(
                    order = orderRepository.createOrder(order)
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_CREATE_ORDER", e)
        OrderResponse(error = ORDER_UNEXPECTED)
    }

    override fun changeOrderDefaultDiscount(orderUUID: UUID, manipulateOrder: ManipulateOrder): OrderResponse = try {
        val order = orderRepository.getOrderByUUID(orderUUID)

        if (order!!.status != OrderStatus.OPEN) {
            OrderResponse(
                error = ORDER_IS_NOT_OPEN
            )
        } else {
            OrderResponse(
                order = orderRepository.getOrderByUUID(orderUUID)
            )
        }
    } catch (e: Exception) {
        logger.error("ERROR_CHANGE_ORDER_DEFAULT_DISCOUNT", e)
        OrderResponse(error = ORDER_UNEXPECTED)
    }

    override fun addOrderItem(
        orderUUID: UUID,
        manipulateOrderItem: ManipulateOrderItem
    ): OrderItemListResponse = try {
        validateOrderItem(orderUUID, manipulateOrderItem, OrderItemValidateType.Quantity).let { validate ->
            if (validate != null) {
                OrderItemListResponse(
                    error = validate
                )
            } else {
                OrderItemListResponse(
                    items = orderRepository.addOrderItem(orderUUID, manipulateOrderItem)
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_ADD_ITEM", e)
        OrderItemListResponse(error = ORDER_UNEXPECTED)
    }

    override fun changeOrderItemDiscount(
        orderUUID: UUID,
        manipulateOrderItem: ManipulateOrderItem
    ): OrderItemListResponse = try {
        validateOrderItem(orderUUID, manipulateOrderItem, OrderItemValidateType.Discount).let { validate ->
            if (validate != null) {
                OrderItemListResponse(
                    error = validate
                )
            } else {
                OrderItemListResponse(
                    items = orderRepository.changeOrderItemDiscount(orderUUID, manipulateOrderItem)
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_CHANGE_ITEM_DISCOUNT", e)
        OrderItemListResponse(error = ORDER_UNEXPECTED)
    }

    override fun removeOrderItem(
        orderUUID: UUID,
        manipulateOrderItem: ManipulateOrderItem
    ): OrderItemListResponse = try {
        validateOrderItem(orderUUID, manipulateOrderItem, OrderItemValidateType.Remove).let { validate ->
            if (validate != null) {
                OrderItemListResponse(
                    error = validate
                )
            } else {
                OrderItemListResponse(
                    items = orderRepository.removeOrderItem(orderUUID, manipulateOrderItem)
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_REMOVE_ORDER_ITEM", e)
        OrderItemListResponse(error = ORDER_UNEXPECTED)
    }

    override fun changeOrderStatus(
        orderUUID: UUID,
        manipulateOrder: ManipulateOrder
    ): OrderResponse {
        try {
            orderRepository.getOrderByUUID(orderUUID) ?: return OrderResponse(error = ORDER_NOT_FOUND)

            return OrderResponse(
                order = orderRepository.changeOrderStatus(orderUUID, manipulateOrder)
            )
        } catch (e: Exception) {
            logger.error("ERROR_CHANGE_ORDER_STATUS", e)
            return OrderResponse(error = ORDER_UNEXPECTED)
        }
    }

    override fun getOrderByUUID(orderUUID: UUID): OrderResponse {
        try {
            val order = orderRepository.getOrderByUUID(orderUUID) ?: return OrderResponse(error = ORDER_NOT_FOUND)

            return OrderResponse(
                order = order
            )
        } catch (e: Exception) {
            logger.error("ERROR_GET_ORDER_BY_UUID", e)
            return OrderResponse(error = ORDER_UNEXPECTED)
        }
    }

    override fun getPaginatedOrder(count: Int, page: Int, params: Map<String, Any?>): OrderPaginationResponse = try {
        OrderPaginationResponse(
            orders = orderRepository.getPaginatedOrder(count, page, params)
        )
    } catch (e: Exception) {
        logger.error("ERROR_GET_PAGINATED_ORDER", e)
        OrderPaginationResponse(error = ORDER_UNEXPECTED)
    }

    private fun validateOrder(order: Order): OrderExceptions? {
        val customer = if (order.customer != null) customerRepository.getCustomerByUUID(order.customer.uuid!!) else null

        return when {
            order.customer == null -> {
                ORDER_CUSTOMER_IS_EMPTY
            }

            customer == null -> {
                ORDER_CUSTOMER_NOT_FOUND
            }

            customer.status != CustomerStatus.FIT -> {
                ORDER_CUSTOMER_CANT_OPEN_ORDER
            }

            else -> null
        }
    }

    private fun validateOrderItem(
        orderUUID: UUID,
        manipulateOrderItem: ManipulateOrderItem,
        validateType: OrderItemValidateType
    ): OrderExceptions? {
        val order = orderRepository.getOrderByUUID(orderUUID) ?: return ORDER_NOT_FOUND

        if (order.status != OrderStatus.OPEN) {
            return ORDER_IS_NOT_OPEN
        }

        val product = productRepository.getProductByUUID(manipulateOrderItem.productUUID)

        if (product == null) {
            return ORDER_PRODUCT_NOT_FOUND
        } else if (product.status != ProductStatus.ACTIVE) {
            return ORDER_PRODUCT_INVALID
        }

        if (validateType == OrderItemValidateType.Quantity) {
            if (manipulateOrderItem.quantity <= 0) {
                return ORDER_INVALID_QUANTITY
            }
        }

        if (validateType == OrderItemValidateType.Discount){
            if(manipulateOrderItem.discount < order.defaultDiscount!!){
                return ORDER_DISCOUNT_LESS_THAN_DEFAULT
            }
        }

        return null
    }

    private enum class OrderItemValidateType{
        Quantity,
        Discount,
        Remove
    }
}