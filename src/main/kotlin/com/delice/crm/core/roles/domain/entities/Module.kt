package com.delice.crm.core.roles.domain.entities

import java.util.UUID

class Module (
    val uuid: UUID? = null,
    val label: String? = "",
    val code: String? = "",
    val path: String? = ""
)

data class DataModule(
    val code: String? = null,
    val path: String? = null,
    val roles: List<DataRole>? = emptyList()
)