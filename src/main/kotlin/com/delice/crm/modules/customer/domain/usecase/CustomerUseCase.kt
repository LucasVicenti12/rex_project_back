package com.delice.crm.modules.customer.domain.usecase

import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.usecase.response.ApprovalCustomerResponse
import com.delice.crm.modules.customer.domain.usecase.response.CustomerEconomicActivities
import com.delice.crm.modules.customer.domain.usecase.response.CustomerPaginationResponse
import com.delice.crm.modules.customer.domain.usecase.response.CustomerResponse
import java.util.*

interface CustomerUseCase {
    fun registerCustomer(customer: Customer): CustomerResponse
    fun queryPreInfoCustomerByDocument(document: String): CustomerResponse
    fun approvalCustomer(status: CustomerStatus, customerUUID: UUID): ApprovalCustomerResponse
    fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): CustomerEconomicActivities
    fun getCustomerByUUID(customer: Customer): CustomerResponse
    fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): CustomerPaginationResponse
}