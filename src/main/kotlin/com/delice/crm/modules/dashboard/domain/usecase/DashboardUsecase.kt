package com.delice.crm.modules.dashboard.domain.usecase

import com.delice.crm.modules.dashboard.domain.usecase.response.DashboardResponse

interface DashboardUsecase {
    fun getDashboardCustomer(): DashboardResponse?
}