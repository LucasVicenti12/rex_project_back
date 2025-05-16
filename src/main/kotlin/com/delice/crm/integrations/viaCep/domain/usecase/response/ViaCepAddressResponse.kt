package com.delice.crm.integrations.viaCep.domain.usecase.response

import com.delice.crm.integrations.viaCep.domain.entities.ViaCepAddress
import com.delice.crm.integrations.viaCep.domain.exceptions.ViaCepAddressException

data class ViaCepAddressResponse(
    val address: ViaCepAddress? = null,
    val error: ViaCepAddressException? = null
)