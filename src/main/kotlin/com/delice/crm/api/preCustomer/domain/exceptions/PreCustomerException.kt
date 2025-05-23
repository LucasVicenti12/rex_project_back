package com.delice.crm.api.preCustomer.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val PRE_CUSTOMER_NOT_FOUND = PreCustomerException("PRE_CUSTOMER_NOT_FOUND", "Customer not found")
val INVALID_DOCUMENT = PreCustomerException("INVALID_DOCUMENT", "Inform an valid document")
val PRE_CUSTOMER_UNEXPECTED_ERROR = PreCustomerException("PRE_CUSTOMER_UNEXPECTED_ERROR", "An unexpected error occurred")

class PreCustomerException(code: String, message: String): DefaultError(code, message)