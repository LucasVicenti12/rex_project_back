package com.delice.crm.modules.order.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val ORDER_UNEXPECTED = OrderExceptions("ORDER_UNEXPECTED", "An unexpected error has occurred")
val ORDER_CUSTOMER_NOT_FOUND = OrderExceptions("ORDER_CUSTOMER_NOT_FOUND", "Customer not found")
val ORDER_CUSTOMER_IS_EMPTY = OrderExceptions("ORDER_CUSTOMER_IS_EMPTY", "Customer must not be empty")
val ORDER_CUSTOMER_CANT_OPEN_ORDER = OrderExceptions("ORDER_CUSTOMER_CANT_OPEN_ORDER", "Customer is not fit to open an order")
val ORDER_IS_NOT_OPEN = OrderExceptions("ORDER_IS_NOT_OPEN", "Order is not open")
val ORDER_PRODUCT_NOT_FOUND = OrderExceptions("ORDER_PRODUCT_NOT_FOUND", "Product not found")
val ORDER_PRODUCT_INVALID = OrderExceptions("ORDER_PRODUCT_INVALID", "Product is invalid")
val ORDER_INVALID_QUANTITY = OrderExceptions("ORDER_INVALID_QUANTITY", "Invalid quantity")
val ORDER_NOT_FOUND = OrderExceptions("ORDER_NOT_FOUND", "Order not found")
val ORDER_DISCOUNT_LESS_THAN_DEFAULT = OrderExceptions("ORDER_DISCOUNT_LESS_THAN_DEFAULT", "Informed discount is less than the order default discount")

class OrderExceptions(code: String, message: String): DefaultError(code = code,  message = message)