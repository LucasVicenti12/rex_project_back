package com.delice.crm.modules.dashboard.domain.usecase

import com.delice.crm.modules.dashboard.domain.usecase.response.DashboardResponse

interface DashboardUsecase {
    fun getDashboardCustomer(): DashboardResponse?
    fun getDashboardOrder(): DashboardResponse?
    fun getDashboardRankBest(): DashboardResponse?
    fun getDashboardRankLess(): DashboardResponse?
    fun getDashboardTotalSold(): DashboardResponse?
    fun getDashboardMostWalletSold(): DashboardResponse?
    fun getDashboardMostOperatorSold(): DashboardResponse?
    fun getDashboardMonthSold(): DashboardResponse?
}