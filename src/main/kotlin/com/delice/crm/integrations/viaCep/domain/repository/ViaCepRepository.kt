package com.delice.crm.integrations.viaCep.domain.repository

import com.delice.crm.integrations.viaCep.domain.entities.ViaCepAddress

interface ViaCepRepository {
    fun getAddressInBase(zipCode: String): ViaCepAddress?
    fun getAddressInViaCepBase(zipCode: String): ViaCepAddress?
    fun saveAddressInBase(address: ViaCepAddress)
}