package com.delice.crm.modules.customer.domain.usecase

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.usecase.response.*
import java.util.*

interface CustomerUseCase {
    fun registerCustomer(customer: Customer, userUUID: UUID): CustomerResponse
    fun updateCustomer(customer: Customer, userUUID: UUID): CustomerResponse
    fun approvalCustomer(status: CustomerStatus, customerUUID: UUID, userUUID: UUID): ApprovalCustomerResponse
    fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): CustomerEconomicActivities
    fun getCustomerByUUID(customerUUID: UUID): CustomerResponse
    fun getCustomerPagination(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): CustomerPaginationResponse
    fun listSimpleCustomer(): SimpleCustomersResponse
}