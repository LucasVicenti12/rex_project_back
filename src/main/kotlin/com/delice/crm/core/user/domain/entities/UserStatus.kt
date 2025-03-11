package com.delice.crm.core.user.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class UserStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1);
}