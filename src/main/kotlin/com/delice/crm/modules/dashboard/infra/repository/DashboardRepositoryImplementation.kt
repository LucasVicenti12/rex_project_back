package com.delice.crm.modules.dashboard.infra.repository

import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.repository.DashboardRepository
import org.springframework.stereotype.Service

@Service
class DashboardRepositoryImplementation: DashboardRepository {
    override fun getDashboardCustomer(): DashboardCustomerValues? {
        TODO("Not yet implemented")
    }
}