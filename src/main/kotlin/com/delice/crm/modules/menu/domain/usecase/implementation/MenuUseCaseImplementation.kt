package com.delice.crm.modules.menu.domain.usecase.implementation

import com.delice.crm.core.config.entities.SystemUser
import com.delice.crm.modules.menu.domain.exceptions.INVALID_QUERY
import com.delice.crm.modules.menu.domain.exceptions.MENU_UNEXPECTED_ERROR
import com.delice.crm.modules.menu.domain.repository.MenuRepository
import com.delice.crm.modules.menu.domain.usecase.MenuUseCase
import com.delice.crm.modules.menu.domain.usecase.response.MenuResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MenuUseCaseImplementation(
    private val menuRepository: MenuRepository
) : MenuUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(MenuUseCaseImplementation::class.java)
    }

    override fun queryMenuOptions(query: String, user: SystemUser): MenuResponse = try {
        if (query.isBlank()) {
            MenuResponse(error = INVALID_QUERY)
        } else {
            MenuResponse(menu = menuRepository.queryMenuOptions(query, user))
        }
    } catch (e: Exception) {
        logger.error("QUERY_MENU_OPTIONS", e)
        MenuResponse(error = MENU_UNEXPECTED_ERROR)
    }
}