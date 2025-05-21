package com.delice.crm.core.utils.contact

import com.delice.crm.core.utils.enums.HasType

enum class ContactType(override val type: String): HasType {
    EMAIL("email"),
    PHONE("phone"),
    MEDIA("media"),
    NONE("none")
}