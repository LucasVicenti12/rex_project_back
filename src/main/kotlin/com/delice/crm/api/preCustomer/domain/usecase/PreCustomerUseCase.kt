package com.delice.crm.api.preCustomer.domain.usecase

import com.delice.crm.api.preCustomer.domain.usecase.response.PreCustomerResponse

interface PreCustomerUseCase {
    fun getPreCustomer(document: String): PreCustomerResponse
}