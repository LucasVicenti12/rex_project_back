package com.delice.crm.api.economicActivities.domain.usecase.reponse

import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.api.economicActivities.domain.exceptions.EconomicActivityException

data class EconomicActivityResponse(
    val economicActivity: EconomicActivity? = null,
    val error: EconomicActivityException? = null
)