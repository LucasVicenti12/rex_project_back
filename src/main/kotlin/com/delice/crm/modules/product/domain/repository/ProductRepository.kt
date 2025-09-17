package com.delice.crm.modules.product.domain.repository

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.entities.SimpleProduct
import java.util.UUID

interface ProductRepository {
    fun createProduct(product: Product): Product?
    fun updateProduct(product: Product): Product?
    fun getProductByUUID(uuid: UUID): Product?
    fun getProductByCode(code: Int): Product?
    fun getProductPagination(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): Pagination<Product>?
    fun saveProductMedia(media: List<ProductMedia>, productUUID: UUID): List<ProductMedia>
    fun getSimpleProducts(): List<SimpleProduct>?
}