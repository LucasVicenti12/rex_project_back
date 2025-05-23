package com.delice.crm.api.economicActivities.domain.usecase

import com.delice.crm.api.economicActivities.domain.usecase.reponse.EconomicActivityResponse

interface EconomicActivityUseCase {
    fun getEconomicActivity(code: String): EconomicActivityResponse
}