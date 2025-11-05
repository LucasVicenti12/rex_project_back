package com.delice.crm.modules.dashboard.domain.usecase.implementation

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

    override fun getDashboardCustomer(): DashboardResponse = try {
        val dashboardCustomer = dashboardRepository.getDashboardCustomer();

        if (dashboardCustomer == null) {
            DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
        } else {
            DashboardResponse(dashboardCustomerValues = dashboardCustomer)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_CUSTOMER_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardOrder(): DashboardResponse = try {
        val dashboardOrder = dashboardRepository.getDashboardOrder();

        if (dashboardOrder == null) {
            DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
        } else {
            DashboardResponse(dashboardOrderValues = dashboardOrder)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_ORDER_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardRank(): DashboardResponse = try {
        val dashboardRank = dashboardRepository.getDashboardRank();

        if (dashboardRank == null) {
            DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
        } else {
            DashboardResponse(dashboardRankValues = dashboardRank)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_RANK_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardTotalSold(): DashboardResponse = try {
        val dashboardTotalSold = dashboardRepository.getDashboardTotalSold();

        if (dashboardTotalSold == 0.0) {
            DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
        } else {
            DashboardResponse(dashboardTotalSold = dashboardTotalSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_TOTAL_SOLD_VALUES", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardMostWalletSold(): DashboardResponse = try {
        val dashboardMostWalletSold = dashboardRepository.getDashboardMostWalletSold();

        if (dashboardMostWalletSold == null) {
            DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
        } else {
            DashboardResponse(dashboardMostWalletSold = dashboardMostWalletSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_MOST_WALLET_SOLD", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }

    override fun getDashboardMostOperatorSold(): DashboardResponse = try {
        val dashboardMostOperatorSold = dashboardRepository.getDashboardMostOperatorSold();

        if (dashboardMostOperatorSold == null) {
            DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
        } else {
            DashboardResponse(dashboardMostOperatorSold = dashboardMostOperatorSold)
        }
    } catch (e: Exception) {
        logger.error("GET_DASHBOARD_MOST_WALLET_SOLD", e)
        DashboardResponse(error = DASHBOARD_UNEXPECTED_ERROR)
    }
}
