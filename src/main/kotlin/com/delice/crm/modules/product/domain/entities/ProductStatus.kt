package com.delice.crm.modules.product.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class ProductStatus(override val code: Int): HasCode {
    ACTIVE(0),
    INACTIVE(1),
}