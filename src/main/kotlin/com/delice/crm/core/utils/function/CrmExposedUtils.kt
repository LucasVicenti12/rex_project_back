package com.delice.crm.core.utils.function

import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.javatime.JavaLocalDateColumnType
import java.time.LocalDate

fun convertDateTimeToDate(expr: Expression<*>): ExpressionWithColumnType<LocalDate> =
    CustomFunction("DATE", JavaLocalDateColumnType(), expr)

fun convertDateTimeToMonth(expr: Expression<*>): ExpressionWithColumnType<Int> =
    CustomFunction("MONTH", IntegerColumnType(), expr)

fun convertDateTimeToYear(expr: Expression<*>): ExpressionWithColumnType<Int> =
    CustomFunction("YEAR", IntegerColumnType(), expr)