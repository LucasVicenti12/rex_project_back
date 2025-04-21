package com.delice.crm.core.utils.filter

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table

interface ExposedFilter<T : Table> {
    fun toFilter(table: T): Op<Boolean>
}

fun String?.parametersToMap(): Map<String, Any?> = try {
    val parameters = this?.split("&") ?: emptyList()

    when {
        parameters.isEmpty() -> {
            emptyMap()
        }

        else -> {
            parameters.mapNotNull {
                val (key, value) = it.split(
                    "=",
                    limit = 2
                ).let { p -> p.getOrNull(0) to p.getOrNull(1) }

                if (key != null && value != null) {
                    key to value
                } else null
            }.toMap()
        }
    }
} catch (e: Exception) {
    emptyMap()
}