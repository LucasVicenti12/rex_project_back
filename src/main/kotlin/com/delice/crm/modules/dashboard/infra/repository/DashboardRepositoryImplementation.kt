package com.delice.crm.modules.dashboard.infra.repository

import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.dashboard.domain.entities.DashboardCustomerValues
import com.delice.crm.modules.dashboard.domain.repository.DashboardRepository

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class DashboardRepositoryImplementation : DashboardRepository{
    override fun getDashboardCustomer(): DashboardCustomerValues = transaction {
        val statusCounts = CustomerDatabase
            .selectAll()
            .map { it[CustomerDatabase.status] }
            .groupingBy<Int, Int> { it }
            .eachCount()

        DashboardCustomerValues(
            pending = statusCounts[CustomerStatus.PENDING.code] ?: 0,
            inactive = statusCounts[CustomerStatus.INACTIVE.code] ?: 0,
            fit = statusCounts[CustomerStatus.FIT.code] ?: 0,
            notFit = statusCounts[CustomerStatus.NOT_FIT.code] ?: 0
        )
    }
}


