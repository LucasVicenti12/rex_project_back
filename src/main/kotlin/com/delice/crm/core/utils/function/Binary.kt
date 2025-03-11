package com.delice.crm.core.utils.function

import org.jetbrains.exposed.sql.statements.api.ExposedBlob

fun binaryToString(a: ExposedBlob?): String? = a?.let { String(it.bytes) }