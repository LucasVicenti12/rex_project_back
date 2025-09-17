package com.delice.crm.modules.product.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.entities.SimpleProduct
import com.delice.crm.modules.product.domain.exceptions.ProductExceptions

data class ProductResponse(
    val product: Product? = null,
    val error: ProductExceptions? = null
)

data class ProductPaginationResponse(
    val products: Pagination<Product>? = null,
    val error: ProductExceptions? = null
)

data class ProductMediaResponse(
    val media: List<ProductMedia>? = emptyList(),
    val error: ProductExceptions? = null
)

data class SimpleProductListResponse(
    val products: List<SimpleProduct>? = null,
    val error: ProductExceptions? = null
)