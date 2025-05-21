package com.delice.crm.shared.economicActivities.domain.usecase.reponse

import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.shared.economicActivities.domain.exceptions.EconomicActivityException

data class EconomicActivityResponse(
    val economicActivity: EconomicActivity? = null,
    val error: EconomicActivityException? = null
)