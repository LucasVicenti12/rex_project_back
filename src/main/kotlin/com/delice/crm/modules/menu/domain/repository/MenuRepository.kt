package com.delice.crm.modules.menu.domain.repository

import com.delice.crm.modules.menu.domain.entities.Menu

interface MenuRepository {
    fun queryMenuOptions(query: String): Menu?
}