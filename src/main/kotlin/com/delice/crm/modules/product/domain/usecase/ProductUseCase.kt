package com.delice.crm.modules.product.domain.usecase

import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.usecase.response.ProductMediaResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductPaginationResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductResponse
import java.util.UUID

interface ProductUseCase {
    fun createProduct(product: Product): ProductResponse
    fun updateProduct(product: Product): ProductResponse
    fun getProductByUUID(uuid: UUID): ProductResponse
    fun getProductPagination(page: Int, count: Int, params: Map<String, Any?>): ProductPaginationResponse
    fun saveProductMedia(media: List<ProductMedia>, productUUID: UUID): ProductMediaResponse
}