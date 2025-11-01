package com.delice.crm.modules.dashboard.domain.usecase.response

import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.exceptions.DashboardExceptions

data class DashboardResponse(
    val dashboardCustomerValues: DashboardCustomerValues? = null,
    val error: DashboardExceptions? = null
)