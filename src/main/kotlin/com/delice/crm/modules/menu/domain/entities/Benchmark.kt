package com.delice.crm.modules.menu.domain.entities

class Benchmark (
    val total: List<BenchmarkTotalValue>? = emptyList()
)

class BenchmarkTotalValue(
    val code: String,
    val count: Int,
)