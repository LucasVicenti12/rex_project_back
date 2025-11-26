package com.delice.crm.modules.dashboard.domain.repository

import com.delice.crm.core.user.domain.entities.SimplesSalesUser
import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankProductsValues
import com.delice.crm.modules.dashboard.domain.entities.MonthlySales
import com.delice.crm.modules.wallet.domain.entities.SimpleWallet

interface DashboardRepository {
    fun getDashboardCustomer(): DashboardCustomerValues?
    fun getDashboardOrder(): DashboardOrderValues?
    fun getDashboardRankBest(): DashboardRankProductsValues?
    fun getDashboardRankLess(): DashboardRankProductsValues?
    fun getDashboardTotalSold(): Double?
    fun getDashboardMostWalletSold(): SimpleWallet?
    fun getDashboardMostOperatorSold(): SimplesSalesUser?
    fun getDashboardMonthSold(): List<MonthlySales>
}