package com.delice.crm.shared.economicActivities.domain.usecase

import com.delice.crm.shared.economicActivities.domain.usecase.reponse.EconomicActivityResponse

interface EconomicActivityUseCase {
    fun getEconomicActivity(code: String): EconomicActivityResponse
}