package com.delice.crm.modules.menu.domain.usecase.response

import com.delice.crm.core.utils.exception.DefaultError
import com.delice.crm.modules.menu.domain.entities.Benchmark
import com.delice.crm.modules.menu.domain.entities.Menu

data class MenuResponse(
    val menu: Menu? = null,
    val error: DefaultError? = null
)

data class BenchmarkResponse(
    val benchmark: Benchmark? = null,
    val error: DefaultError? = null
)