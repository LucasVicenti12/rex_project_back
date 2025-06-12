package com.delice.crm.modules.menu.domain.usecase.implementation

import com.delice.crm.modules.menu.domain.repository.MenuRepository
import com.delice.crm.modules.menu.domain.usecase.MenuUseCase
import com.delice.crm.modules.menu.domain.usecase.response.MenuResponse
import com.delice.crm.modules.product.domain.usecase.implementation.ProductUseCaseImplementation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MenuUseCaseImplementation(
    private val menuRepository: MenuRepository
) : MenuUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(MenuUseCaseImplementation::class.java)
    }

    override fun queryMenuOptions(query: String): MenuResponse {
        try {
            return MenuResponse(error = null)
        } catch (e: Exception) {
            return MenuResponse(error = null)
        }
    }
}