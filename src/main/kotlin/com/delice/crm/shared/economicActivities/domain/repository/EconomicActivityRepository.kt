package com.delice.crm.shared.economicActivities.domain.repository

import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivity

interface EconomicActivityRepository {
    fun getEconomicActivityInBase(code: String): EconomicActivity?
    fun getEconomicActivityInAPIBase(code: String): EconomicActivity?
    fun saveEconomicActivityInBase(activity: EconomicActivity)
}