package com.delice.crm.modules.customer.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val CUSTOMER_UNEXPECTED = CustomerExceptions("CUSTOMER_UNEXPECTED", "An unexpected error has occurred")
val COMPANY_NAME_IS_EMPTY = CustomerExceptions("COMPANY_NAME_IS_EMPTY", "The company's name must be provided")
val TRADING_NAME_IS_EMPTY = CustomerExceptions("TRADING_NAME_IS_EMPTY", "The trading's name must be provided")
val PERSON_NAME_IS_EMPTY = CustomerExceptions("PERSON_NAME_IS_EMPTY", "The person's name must be provided")
val DOCUMENT_MUST_PROVIDED = CustomerExceptions("DOCUMENT_MUST_PROVIDED", "The document must be provided")
val CUSTOMER_ALREADY_EXISTS = CustomerExceptions("CUSTOMER_ALREADY_EXISTS", "Company whit this document already exists")
val CUSTOMER_CONTACTS_IS_EMPTY = CustomerExceptions("CUSTOMER_CONTACTS_IS_EMPTY", "At least one contact must be provided")
val CUSTOMER_STATE_IS_EMPTY = CustomerExceptions("CUSTOMER_STATE_IS_EMPTY", "The state must be provided")
val CUSTOMER_CITY_IS_EMPTY = CustomerExceptions("CUSTOMER_CITY_IS_EMPTY", "The city must be provided")
val CUSTOMER_ZIP_CODE_IS_EMPTY = CustomerExceptions("CUSTOMER_ZIP_CODE_IS_EMPTY", "The zip code must be provided")
val CUSTOMER_ADDRESS_IS_EMPTY = CustomerExceptions("CUSTOMER_ADDRESS_IS_EMPTY", "The address must be provided")
val CUSTOMER_COMPLEMENT_IS_EMPTY = CustomerExceptions("CUSTOMER_COMPLEMENT_IS_EMPTY", "The address must be provided")
val CUSTOMER_ADDRESS_NUMBER_IS_EMPTY = CustomerExceptions("CUSTOMER_ADDRESS_NUMBER_IS_EMPTY", "The address number must be provided")
val CUSTOMER_ECONOMIC_ACTIVITIES_IS_EMPTY = CustomerExceptions("CUSTOMER_ECONOMIC_ACTIVITIES_IS_EMPTY", "The economic activities be provided")

class CustomerExceptions(code: String, message: String) : DefaultError(code, message)