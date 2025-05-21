package com.delice.crm.modules.customer.domain.usecase.implementation

import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.customer.domain.usecase.CustomerUseCase
import com.delice.crm.modules.customer.domain.usecase.response.ApprovalCustomerResponse
import com.delice.crm.modules.customer.domain.usecase.response.CustomerEconomicActivities
import com.delice.crm.modules.customer.domain.usecase.response.CustomerPaginationResponse
import com.delice.crm.modules.customer.domain.usecase.response.CustomerResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerUseCaseImplementation(
    private val customerRepository: CustomerRepository
): CustomerUseCase {
    override fun registerCustomer(customer: Customer): CustomerResponse {
        TODO("Not yet implemented")
    }

    override fun queryPreInfoCustomerByDocument(document: String): CustomerResponse {
        TODO("Not yet implemented")
    }

    override fun approvalCustomer(status: CustomerStatus, customerUUID: UUID): ApprovalCustomerResponse {
        TODO("Not yet implemented")
    }

    override fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): CustomerEconomicActivities {
        TODO("Not yet implemented")
    }

    override fun getCustomerByUUID(customer: Customer): CustomerResponse {
        TODO("Not yet implemented")
    }

    override fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): CustomerPaginationResponse {
        TODO("Not yet implemented")
    }
}