package com.delice.crm.modules.map.domain.usecase
import com.delice.crm.modules.map.domain.usecase.response.MapResponse

interface MapUseCase {
    fun getMapCustomerForState(): MapResponse
}