package com.delice.crm.api.preCustomer.domain.usecase.response

import com.delice.crm.api.preCustomer.domain.entities.PreCustomer
import com.delice.crm.api.preCustomer.domain.exceptions.PreCustomerException

data class PreCustomerResponse(
    val customer: PreCustomer? = null,
    val error: PreCustomerException? = null,
)