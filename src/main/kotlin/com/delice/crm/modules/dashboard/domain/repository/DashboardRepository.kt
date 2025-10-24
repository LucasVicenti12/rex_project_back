package com.delice.crm.modules.dashboard.domain.repository

import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues

interface DashboardRepository {
    fun getDashboardCustomer(): DashboardCustomerValues?
}