package com.delice.crm.shared.viaCep.domain.repository

import com.delice.crm.shared.viaCep.domain.entities.ViaCepAddress

interface ViaCepRepository {
    fun getAddressInBase(zipCode: String): ViaCepAddress?
    fun getAddressInViaCepBase(zipCode: String): ViaCepAddress?
    fun saveAddressInBase(address: ViaCepAddress)
}