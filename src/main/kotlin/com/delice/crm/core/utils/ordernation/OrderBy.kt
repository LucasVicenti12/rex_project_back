package com.delice.crm.core.utils.ordernation

import com.delice.crm.core.utils.enums.HasType
import com.delice.crm.core.utils.enums.enumFromTypeValue

@JvmInline
value class OrderBy(private val raw: String) {
    val orderBy: String
        get() = raw.substringBefore(":")

    val sortBy: SortBy
        get() {
            val v = raw.substringAfter(":", "").uppercase()

            if (v.isBlank()) {
                return SortBy.ASC
            }

            return enumFromTypeValue<SortBy, String>(v)
        }
}

enum class SortBy(override val type: String): HasType {
    ASC("ASC"),
    DESC("DESC")
}