package com.delice.crm.shared.economicActivities.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val INVALID_ECONOMIC_ACTIVITY_NOT_FOUND = EconomicActivityException("INVALID_ECONOMIC_ACTIVITY_NOT_FOUND", "Economic activity not found")
val INVALID_ECONOMIC_ACTIVITY = EconomicActivityException("INVALID_ECONOMIC_ACTIVITY", "Inform an valid economic activity code")
val ECONOMIC_ACTIVITY_UNEXPECTED_ERROR = EconomicActivityException("ECONOMIC_ACTIVITY_UNEXPECTED_ERROR", "An unexpected error occurred")

class EconomicActivityException(code: String, message: String): DefaultError(code, message)