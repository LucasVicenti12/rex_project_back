package com.delice.crm.modules.dashboard.domain.repository

import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankValues

interface DashboardRepository {
    fun getDashboardCustomer(): DashboardCustomerValues?
    fun getDashboardOrder(): DashboardOrderValues?
    fun getDashboardRank(): DashboardRankValues?
}