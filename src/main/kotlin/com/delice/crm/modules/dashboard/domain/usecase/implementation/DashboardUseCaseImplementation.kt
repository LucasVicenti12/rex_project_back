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
}
