package com.delice.crm.integrations.viaCep.domain.usecase

import com.delice.crm.integrations.viaCep.domain.entities.ViaCepAddress

interface ViaCepUseCase {
    fun getAddressInViaCepBase(zipCode: Int): ViaCepAddress?
    fun getQueryAddressInViaCepBase(address: String): List<ViaCepAddress>?
}