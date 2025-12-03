package com.delice.crm.modules.map.domain.usecase.response

import com.delice.crm.modules.map.domain.entities.MapCustomerForState
import com.delice.crm.modules.map.domain.exceptions.MapExceptions

data class MapResponse (
    val customersByState: List<MapCustomerForState>? = null,
    val error: MapExceptions? = null
)