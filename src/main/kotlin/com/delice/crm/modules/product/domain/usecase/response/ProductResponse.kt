package com.delice.crm.modules.product.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.exceptions.ProductExceptions

data class ProductResponse(
    val product: Product? = null,
    val error: ProductExceptions? = null
)

data class ProductPaginationResponse(
    val products: Pagination<Product>? = null,
    val error: ProductExceptions? = null
)