package com.delice.crm.modules.dashboard.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val DASHBOARD_UNEXPECTED_ERROR = DashboardExceptions("DASHBOARD_UNEXPECTED_ERROR", "An unexpected error has occurred")

class DashboardExceptions(code: String, message: String): DefaultError(code = code, message = message)