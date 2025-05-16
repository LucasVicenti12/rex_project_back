package com.delice.crm.integrations.viaCep.domain.repository

import com.delice.crm.integrations.viaCep.domain.entities.ViaCepAddress

interface ViaCepRepository {
    fun getAddressInBase(zipCode: Int): ViaCepAddress?
    fun getAddressInViaCepBase(zipCode: Int): ViaCepAddress?
    fun getQueryAddressInViaCepBase(address: String): List<ViaCepAddress>?
    fun saveAddressInBase(address: ViaCepAddress)
}