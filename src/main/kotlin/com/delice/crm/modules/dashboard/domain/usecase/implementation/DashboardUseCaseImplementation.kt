package com.delice.crm.modules.dashboard.domain.usecase.implementation

import com.delice.crm.modules.dashboard.domain.exceptions.DASHBOARD_NOT_FOUND
import com.delice.crm.modules.dashboard.domain.exceptions.DASHBOARD_UNEXPECTED_ERROR
import com.delice.crm.modules.dashboard.domain.repository.DashboardRepository
import com.delice.crm.modules.dashboard.domain.usecase.DashboardUsecase
import com.delice.crm.modules.dashboard.domain.usecase.response.DashboardResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DashboardUseCaseImplementation (
    private val dashboardRepository: DashboardRepository
) : DashboardUsecase {
    companion object{
        private val logger = LoggerFactory.getLogger(DashboardUseCaseImplementation::class.java)
    }

    override fun getDashboardCustomer(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardCustomer = dashboardRepository.getDashboardCustomer(params);

        if (dashboardCustomer == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardCustomerValues = dashboardCustomer)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_CUSTOMER_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardOrder(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardOrder = dashboardRepository.getDashboardOrder(params);

        if (dashboardOrder == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardOrderValues = dashboardOrder)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_ORDER_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardRankBest(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardRank = dashboardRepository.getDashboardRankBest(params);

        if (dashboardRank == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardRankValuesBest = dashboardRank)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_RANK_VALUES_BEST", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardRankLess(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardRank = dashboardRepository.getDashboardRankLess(params);

        if (dashboardRank == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardRankValuesLess = dashboardRank)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_RANK_VALUES_LESS", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardTotalSold(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardTotalSold = dashboardRepository.getDashboardTotalSold(params);

        if (dashboardTotalSold == 0.0) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardTotalSold = dashboardTotalSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_TOTAL_SOLD_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardMostWalletSold(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardMostWalletSold = dashboardRepository.getDashboardMostWalletSold(params);

        if (dashboardMostWalletSold == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardMostWalletSold = dashboardMostWalletSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_MOST_WALLET_SOLD", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardMostOperatorSold(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardMostOperatorSold = dashboardRepository.getDashboardMostOperatorSold(params);

        if (dashboardMostOperatorSold == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardMostOperatorSold = dashboardMostOperatorSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_MOST_WALLET_SOLD", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardMonthSold(params: Map<String, Any?>): DashboardResponse = try {
        val dashboardMonthSold = dashboardRepository.getDashboardMonthSold(params);

        if (dashboardMonthSold == null) {
            DashboardResponse(error = DASHBOARD_NOT_FOUND)
        } else {
            DashboardResponse(dashboardMonthSold = dashboardMonthSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_MONTH_SOLD", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }
}