package com.delice.crm.modules.menu.domain.entities

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

class MenuOptionValue(
    val uuid: UUID,
    val value: String
)