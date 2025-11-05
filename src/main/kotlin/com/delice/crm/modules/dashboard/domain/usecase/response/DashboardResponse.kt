package com.delice.crm.modules.dashboard.domain.usecase.response

import com.delice.crm.core.user.domain.entities.SimplesSalesUser
import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankValues
import com.delice.crm.modules.dashboard.domain.exceptions.DashboardExceptions
import com.delice.crm.modules.wallet.domain.entities.SimpleWallet

data class DashboardResponse(
    val dashboardCustomerValues: DashboardCustomerValues? = null,
    val dashboardOrderValues: DashboardOrderValues? = null,
    val dashboardRankValues: DashboardRankValues? = null,
    val dashboardTotalSold: Double? = null,
    val dashboardMostWalletSold: SimpleWallet? = null,
    val dashboardMostOperatorSold: SimplesSalesUser? = null,
    val error: DashboardExceptions? = null
)