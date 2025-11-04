package com.delice.crm.modules.dashboard.domain.usecase.response

import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankValues
import com.delice.crm.modules.dashboard.domain.exceptions.DashboardExceptions

data class DashboardResponse(
    val dashboardCustomerValues: DashboardCustomerValues? = null,
    val dashboardOrderValues: DashboardOrderValues? = null,
    val dashboardRankValues: DashboardRankValues? = null,
    val error: DashboardExceptions? = null
)