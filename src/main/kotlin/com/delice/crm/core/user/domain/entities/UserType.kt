package com.delice.crm.core.user.domain.entities

import com.delice.crm.core.utils.enums.HasType

enum class UserType(override val type: String): HasType {
    DEV("dev"),
    OWNER("owner"),
    EMPLOYEE("employee");
}