package com.delice.crm.api.viaCep.domain.usecase

import com.delice.crm.api.viaCep.domain.usecase.response.ViaCepAddressResponse

interface ViaCepUseCase {
    fun getAddressInViaCepBase(zipCode: String): ViaCepAddressResponse
}