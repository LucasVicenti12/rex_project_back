package com.delice.crm.shared.viaCep.domain.usecase.response

import com.delice.crm.shared.viaCep.domain.entities.ViaCepAddress
import com.delice.crm.shared.viaCep.domain.exceptions.ViaCepAddressException

data class ViaCepAddressResponse(
    val address: ViaCepAddress? = null,
    val error: ViaCepAddressException? = null
)