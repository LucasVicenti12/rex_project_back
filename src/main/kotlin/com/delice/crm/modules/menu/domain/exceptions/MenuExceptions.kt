package com.delice.crm.modules.menu.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val INVALID_QUERY = MenuExceptions("INVALID_QUERY", "Inform an valid query")
val MENU_UNEXPECTED_ERROR = MenuExceptions("MENU_UNEXPECTED_ERROR", "Inform an unexpected error")

class MenuExceptions(code: String, message: String) : DefaultError(code = code, message = message)