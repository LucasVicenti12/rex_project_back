package com.delice.crm.api.viaCep.domain.usecase.response

import com.delice.crm.api.viaCep.domain.entities.ViaCepAddress
import com.delice.crm.api.viaCep.domain.exceptions.ViaCepAddressException

data class ViaCepAddressResponse(
    val address: ViaCepAddress? = null,
    val error: ViaCepAddressException? = null
)