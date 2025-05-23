package com.delice.crm.api.economicActivities.domain.repository

import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity

interface EconomicActivityRepository {
    fun getEconomicActivityInBase(code: String): EconomicActivity?
    fun getEconomicActivityInAPIBase(code: String): EconomicActivity?
    fun saveEconomicActivityInBase(activity: EconomicActivity)
}