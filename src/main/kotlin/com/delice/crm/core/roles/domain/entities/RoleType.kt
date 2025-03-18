package com.delice.crm.core.roles.domain.entities

import com.delice.crm.core.utils.enums.HasType

enum class RoleType(override val type: String): HasType {
    DEV("DEV"),
    USER("USER")
}