package com.delice.crm.modules.menu.domain.usecase

import com.delice.crm.core.config.entities.SystemUser
import com.delice.crm.modules.menu.domain.usecase.response.MenuResponse

interface MenuUseCase {
    fun queryMenuOptions(query: String, user: SystemUser): MenuResponse
}