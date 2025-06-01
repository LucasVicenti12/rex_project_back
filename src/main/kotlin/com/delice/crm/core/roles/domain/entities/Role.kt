package com.delice.crm.core.roles.domain.entities

import java.util.*

class Role (
    val uuid: UUID? = null,
    val label: String? = null,
    val code: String? = null,
    val roleType: RoleType? = RoleType.USER,
    val moduleUUID: UUID? = null,
)

data class DataRole(
    val uuid: UUID? = null,
    val code: String? = null,
    val label: String? = null,
)