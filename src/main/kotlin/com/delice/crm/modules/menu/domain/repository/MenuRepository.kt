package com.delice.crm.modules.menu.domain.repository

import com.delice.crm.core.config.entities.SystemUser
import com.delice.crm.modules.menu.domain.entities.Benchmark
import com.delice.crm.modules.menu.domain.entities.Menu

interface MenuRepository {
    fun queryMenuOptions(query: String, user: SystemUser): Menu?
    fun getHomeResume(): Benchmark?
}