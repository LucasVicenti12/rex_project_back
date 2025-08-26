package com.delice.crm.core.utils.filter

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table

interface ExposedOrderBy<T : Table> {
    fun toOrderBy(): Pair<Expression<*>, SortOrder>
}