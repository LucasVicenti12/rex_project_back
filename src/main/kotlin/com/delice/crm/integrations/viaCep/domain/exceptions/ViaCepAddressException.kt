package com.delice.crm.integrations.viaCep.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val ADDRESS_NOT_FOUND = ViaCepAddressException("ADDRESS_NOT_FOUND", "Address not found")

class ViaCepAddressException(code: String, message: String): DefaultError(code, message)