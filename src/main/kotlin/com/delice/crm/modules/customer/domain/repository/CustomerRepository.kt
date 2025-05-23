package com.delice.crm.modules.customer.domain.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import java.util.UUID

interface CustomerRepository {
    fun registerCustomer(customer: Customer): Customer?
    fun queryPreInfoCustomerByDocumentInBase(document: String): Customer?
    fun approvalCustomer(status: CustomerStatus, customerUUID: UUID)
    fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): List<EconomicActivity>?
    fun getCustomerByUUID(customer: Customer): Customer?
    fun getCustomerByDocument(customer: Customer): Customer?
    fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<Customer>?
}