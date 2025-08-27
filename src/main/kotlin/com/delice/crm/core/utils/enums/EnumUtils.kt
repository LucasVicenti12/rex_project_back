package com.delice.crm.core.utils.enums

inline fun <reified T : Enum<T>, K> enumFromTypeValue(value: K): T {
    return enumValues<T>().firstOrNull {
        (it as? HasType)?.type?.equals(value as String, ignoreCase = true) == true ||
        (it as? HasCode)?.code === value
    } ?: throw IllegalArgumentException("Invalid enum type: $value")
}