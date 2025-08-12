package com.delice.crm.modules.customer.domain.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.modules.customer.domain.entities.SimpleCustomer
import java.util.UUID

interface CustomerRepository {
    fun registerCustomer(customer: Customer, userUUID: UUID): Customer?
    fun updateCustomer(customer: Customer, userUUID: UUID): Customer?
    fun approvalCustomer(status: CustomerStatus, customerUUID: UUID, userUUID: UUID)
    fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): List<EconomicActivity>?
    fun getCustomerByUUID(customerUUID: UUID): Customer?
    fun getCustomerByDocument(document: String): Customer?
    fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<Customer>?
    fun listSimpleCustomer(): List<SimpleCustomer>?
    fun attachCustomerToCard(customerUUID: UUID, cardUUID: UUID)
}