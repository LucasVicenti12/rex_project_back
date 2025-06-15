package com.delice.crm.modules.menu.domain.entities

import com.delice.crm.core.utils.enums.HasType
import java.util.UUID

data class Menu (
    val query: String,
    val totalResults: Int,
    val result: List<MenuOption>,
)

data class MenuOption(
    val type: String,
    val values: List<MenuOptionValue>
)

enum class MenuOptionType(override val type: String): HasType {
    Customer("CUSTOMER_MODULE"),
    User("USER_MODULE"),
    Wallet("WALLET_MODULE"),
    Product("PRODUCT_MODULE")
}

class MenuOptionValue(
    val uuid: UUID,
    val value: String
)