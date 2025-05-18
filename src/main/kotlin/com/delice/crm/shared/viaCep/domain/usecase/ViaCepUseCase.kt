package com.delice.crm.shared.viaCep.domain.usecase

import com.delice.crm.shared.viaCep.domain.usecase.response.ViaCepAddressResponse

interface ViaCepUseCase {
    fun getAddressInViaCepBase(zipCode: String): ViaCepAddressResponse
}