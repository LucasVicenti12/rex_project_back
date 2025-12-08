package com.delice.crm.modules.dashboard.domain.repository

import com.delice.crm.core.user.domain.entities.SimplesSalesUser
import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardOrderValues
import com.delice.crm.modules.dashboard.domain.entities.DashboardRankProductsValues
import com.delice.crm.modules.dashboard.domain.entities.MonthlySales
import com.delice.crm.modules.wallet.domain.entities.SimpleWallet

interface DashboardRepository {
    fun getDashboardCustomer(params: Map<String, Any?> = emptyMap()): DashboardCustomerValues?
    fun getDashboardOrder(params: Map<String, Any?> = emptyMap()): DashboardOrderValues?
    fun getDashboardRankBest(params: Map<String, Any?> = emptyMap()): DashboardRankProductsValues?
    fun getDashboardRankLess(params: Map<String, Any?> = emptyMap()): DashboardRankProductsValues?
    fun getDashboardTotalSold(params: Map<String, Any?> = emptyMap()): Double?
    fun getDashboardMostWalletSold(params: Map<String, Any?> = emptyMap()): SimpleWallet?
    fun getDashboardMostOperatorSold(params: Map<String, Any?> = emptyMap()): SimplesSalesUser?
    fun getDashboardMonthSold(params: Map<String, Any?> = emptyMap()): List<MonthlySales>
}