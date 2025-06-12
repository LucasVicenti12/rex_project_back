package com.delice.crm.modules.menu.infra.repository

import com.delice.crm.modules.menu.domain.entities.Menu
import com.delice.crm.modules.menu.domain.repository.MenuRepository
import org.springframework.stereotype.Service

@Service
class MenuRepositoryImplementation: MenuRepository {
    override fun queryMenuOptions(query: String): Menu? {
        TODO("Not yet implemented")
    }
}