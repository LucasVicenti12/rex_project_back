package com.delice.crm.integrations.viaCep.domain.usecase

import com.delice.crm.integrations.viaCep.domain.usecase.response.ViaCepAddressResponse

interface ViaCepUseCase {
    fun getAddressInViaCepBase(zipCode: String): ViaCepAddressResponse
}