package com.delice.crm.modules.product.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val PRODUCT_UNEXPECTED_ERROR = ProductExceptions("PRODUCT_UNEXPECTED_ERROR", "An unexpected error occurred")
val PRODUCT_NOT_FOUND = ProductExceptions("PRODUCT_NOT_FOUND", "Product not found")
val PRODUCT_NAME_IS_EMPTY = ProductExceptions("PRODUCT_NAME_IS_EMPTY", "Product name is empty")
val PRODUCT_CODE_IS_EMPTY = ProductExceptions("PRODUCT_CODE_IS_EMPTY", "Product code is empty")
val PRODUCT_ALREADY_EXISTS = ProductExceptions("PRODUCT_ALREADY_EXISTS", "Product already exists")
val PRODUCT_PRICE_IS_EMPTY = ProductExceptions("PRODUCT_PRICE_IS_EMPTY", "Product price is empty")
val PRODUCT_WEIGHT_IS_EMPTY = ProductExceptions("PRODUCT_WEIGHT_IS_EMPTY", "Product weight is empty")

class ProductExceptions(code: String, message: String): DefaultError(code = code, message = message)