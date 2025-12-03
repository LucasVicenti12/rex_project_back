package com.delice.crm.modules.map.domain.repository

import com.delice.crm.modules.map.domain.entities.MapCustomerForState


interface MapRepository {
    fun getMapCustomerForState(): List<MapCustomerForState>?
}