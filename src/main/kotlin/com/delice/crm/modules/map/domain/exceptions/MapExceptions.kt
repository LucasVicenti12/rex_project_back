package com.delice.crm.modules.map.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val MAP_UNEXPECTED_ERROR = MapExceptions("MAP_UNEXPECTED_ERROR", "An unexpected error has occurred")
val MAP_NOT_FOUND = MapExceptions("MAP_NOT_FOUND", "Date not found")

class MapExceptions(code: String, message: String): DefaultError(code = code, message = message)