package com.delice.crm.core.roles.domain.entities

import com.delice.crm.core.utils.enums.HasType
import java.util.UUID

class Module(
    val uuid: UUID? = null,
    val label: String? = "",
    val code: String? = "",
    val path: String? = ""
)

data class DataModule(
    val uuid: UUID? = null,
    val label: String? = null,
    val code: String? = null,
    val path: String? = null,
    val roles: List<DataRole>? = emptyList()
)

enum class CrmModule(override val type: String) : HasType {
    Wallet("WALLET"),
    Customer("CUSTOMER"),
    User("USER_MODULE"),
    Product("PRODUCT")
}