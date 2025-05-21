package com.delice.crm.modules.customer.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class CustomerStatus(override val code: Int): HasCode {
    PENDING(0),
    FIT(1),
    NOT_FIT(2),
    INACTIVE(3),
}