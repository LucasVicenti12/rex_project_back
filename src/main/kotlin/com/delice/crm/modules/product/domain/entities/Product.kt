package com.delice.crm.modules.product.domain.entities

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

class Product (
    val uuid: UUID? = null,
    val name: String? = null,
    val code: Int? = 0,
    val description: String? = null,
    var images: List<ProductMedia>? = emptyList(),
    val price: Double? = 0.0,
    val weight: Double? = 0.0,
    val status: ProductStatus? = ProductStatus.ACTIVE,
    val createdAt: LocalDate? = LocalDate.now(),
    val modifiedAt: LocalDate? = LocalDate.now(),
)

class ProductMedia(
    val uuid: UUID? = null,
    val image: String? = null,
    val isPrincipal: Boolean? = false,
    val createdAt: LocalDate? = null,
    val modifiedAt: LocalDate? = null
)

data class SimpleProduct(
    val uuid: UUID,
    val name: String
)

@Serializable
data class SerializableProduct(
    val uuid: String,
    val code: Int? = null,
    val name: String? = null,
    val image: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val weight: Double? = null
)