package com.delice.crm.modules.product.domain.entities

import java.time.LocalDate
import java.util.UUID

class Product (
    val uuid: UUID? = null,
    val name: String? = null,
    val code: String? = null,
    val description: String? = null,
    val image: String? = null,
    val price: Double? = 0.0,
    val weight: Double? = 0.0,
    val status: ProductStatus? = ProductStatus.ACTIVE,
    val createdAt: LocalDate? = LocalDate.now(),
    val modifiedAt: LocalDate? = LocalDate.now(),
)