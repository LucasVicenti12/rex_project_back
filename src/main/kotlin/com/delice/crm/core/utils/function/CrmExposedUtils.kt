package com.delice.crm.core.utils.function

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.javatime.JavaLocalDateColumnType
import org.jetbrains.exposed.sql.vendors.MysqlDialect
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import java.time.LocalDate
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.append
import org.jetbrains.exposed.sql.vendors.currentDialect

class DateFunction(val expr: Expression<*>) :
    Function<LocalDate>(JavaLocalDateColumnType()) {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.apply {
            when (currentDialect) {
                is MysqlDialect -> append("DATE(", expr, ")")
                is PostgreSQLDialect -> append("CAST(", expr, " AS DATE)")
            }
        }
    }
}

class MonthFunction(val expr: Expression<*>) :
    Function<Int>(IntegerColumnType()) {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.apply {
            when (currentDialect) {
                is MysqlDialect -> append("MONTH(", expr, ")")
                is PostgreSQLDialect -> append("EXTRACT(MONTH FROM ", expr, ")")
            }
        }
    }
}

class YearFunction(val expr: Expression<*>) :
    Function<Int>(IntegerColumnType()) {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.apply {
            when (currentDialect) {
                is MysqlDialect -> append("YEAR(", expr, ")")
                is PostgreSQLDialect -> append("EXTRACT(YEAR FROM ", expr, ")")
            }
        }
    }
}

fun convertDateTimeToDate(expr: Expression<*>): ExpressionWithColumnType<LocalDate> =
    DateFunction(expr)

fun convertDateTimeToMonth(expr: Expression<*>): ExpressionWithColumnType<Int> =
    MonthFunction(expr)

fun convertDateTimeToYear(expr: Expression<*>): ExpressionWithColumnType<Int> =
    YearFunction(expr)