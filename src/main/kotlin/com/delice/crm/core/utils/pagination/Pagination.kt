package com.delice.crm.core.utils.pagination

data class Pagination<T>(
    val page: Int? = 0,
    val total: Int? = 0,
    val items: List<T>? = listOf(),
)