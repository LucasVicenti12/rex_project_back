package com.delice.crm.integrations.viaCep.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val ADDRESS_NOT_FOUND = ViaCepAddressException("ADDRESS_NOT_FOUND", "Address not found")
val INVALID_ZIP_CODE = ViaCepAddressException("INVALID_ZIP_CODE", "Inform an valid zip code")
val ADDRESS_UNEXPECTED_ERROR = ViaCepAddressException("ADDRESS_UNEXPECTED_ERROR", "An unexpected error occurred")

class ViaCepAddressException(code: String, message: String): DefaultError(code, message)