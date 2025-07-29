package com.delice.crm.modules.menu.domain.entities

import com.delice.crm.core.utils.enums.HasType

class Benchmark(
    val resume: List<BenchmarkTotalValue>? = emptyList()
)

class BenchmarkTotalValue(
    val code: String,
    val count: Int,
)

enum class BenchmarkCode(override val type: String) : HasType {
    PENDING_CUSTOMER("PENDING_CUSTOMER"),
    APPROVED_CUSTOMER("APPROVED_CUSTOMER"),
    ACTIVE_PRODUCT("ACTIVE_PRODUCT"),
    ACTIVE_WALLET("ACTIVE_WALLET"),
}