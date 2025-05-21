package com.delice.crm.modules.customer.infra.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivity
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerRepositoryImplementation: CustomerRepository {
    override fun registerCustomer(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override fun queryPreInfoCustomerByDocumentInBase(document: String): Customer? {
        TODO("Not yet implemented")
    }

    override fun approvalCustomer(status: CustomerStatus, customerUUID: UUID) {
        TODO("Not yet implemented")
    }

    override fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): List<EconomicActivity>? {
        TODO("Not yet implemented")
    }

    override fun getCustomerByUUID(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override fun getCustomerByDocument(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<Customer>? {
        TODO("Not yet implemented")
    }
}