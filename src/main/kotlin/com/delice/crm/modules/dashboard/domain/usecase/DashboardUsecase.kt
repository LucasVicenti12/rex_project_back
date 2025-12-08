package com.delice.crm.modules.dashboard.domain.usecase

import com.delice.crm.modules.dashboard.domain.usecase.response.DashboardResponse

interface DashboardUsecase {
    fun getDashboardCustomer(params: Map<String, Any?>): DashboardResponse
    fun getDashboardOrder(params: Map<String, Any?>): DashboardResponse
    fun getDashboardRankBest(params: Map<String, Any?>): DashboardResponse
    fun getDashboardRankLess(params: Map<String, Any?>): DashboardResponse
    fun getDashboardTotalSold(params: Map<String, Any?>): DashboardResponse
    fun getDashboardMostWalletSold(params: Map<String, Any?>): DashboardResponse
    fun getDashboardMostOperatorSold(params: Map<String, Any?>): DashboardResponse
    fun getDashboardMonthSold(params: Map<String, Any?>): DashboardResponse
}