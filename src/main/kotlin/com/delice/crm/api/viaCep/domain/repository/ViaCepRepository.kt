package com.delice.crm.api.viaCep.domain.repository

import com.delice.crm.api.viaCep.domain.entities.ViaCepAddress

interface ViaCepRepository {
    fun getAddressInBase(zipCode: String): ViaCepAddress?
    fun getAddressInViaCepBase(zipCode: String): ViaCepAddress?
    fun saveAddressInBase(address: ViaCepAddress)
}