package com.delice.crm.modules.customer.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.exceptions.CustomerExceptions
import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivity

data class CustomerResponse(
    val customer: Customer? = null,
    val error: CustomerExceptions? = null
)

data class CustomerPaginationResponse(
    val customers: Pagination<Customer>? = null,
    val error: CustomerExceptions? = null
)

data class CustomerEconomicActivities(
    val activities: List<EconomicActivity>? = emptyList(),
    val error: CustomerExceptions? = null
)

data class ApprovalCustomerResponse(
    val ok: Boolean? = false,
    val error: CustomerExceptions? = null
)