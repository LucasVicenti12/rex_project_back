package com.delice.crm.api.economicActivities.domain.usecase.implementation

import com.delice.crm.core.utils.extensions.removeSpecialChars
import com.delice.crm.api.economicActivities.domain.exceptions.ECONOMIC_ACTIVITY_UNEXPECTED_ERROR
import com.delice.crm.api.economicActivities.domain.exceptions.INVALID_ECONOMIC_ACTIVITY
import com.delice.crm.api.economicActivities.domain.exceptions.INVALID_ECONOMIC_ACTIVITY_NOT_FOUND
import com.delice.crm.api.economicActivities.domain.repository.EconomicActivityRepository
import com.delice.crm.api.economicActivities.domain.usecase.EconomicActivityUseCase
import com.delice.crm.api.economicActivities.domain.usecase.reponse.EconomicActivityResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EconomicActivityUseCaseImplementation(
    private val economicActivityRepository: EconomicActivityRepository
) : EconomicActivityUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(EconomicActivityUseCaseImplementation::class.java)
    }

    override fun getEconomicActivity(code: String): EconomicActivityResponse {
        try {
            val query = code.removeSpecialChars()

            if (query.isBlank() || query.length != 5) {
                return EconomicActivityResponse(error = INVALID_ECONOMIC_ACTIVITY)
            }

            var economicActivity = economicActivityRepository.getEconomicActivityInBase(query)

            if (economicActivity == null) {
                economicActivity = economicActivityRepository.getEconomicActivityInAPIBase(query)

                if (economicActivity == null) {
                    return EconomicActivityResponse(error = INVALID_ECONOMIC_ACTIVITY_NOT_FOUND)
                }

                economicActivityRepository.saveEconomicActivityInBase(economicActivity)
            }

            return EconomicActivityResponse(economicActivity = economicActivity)
        } catch (e: Exception) {
            logger.error("ECONOMIC_ACTIVITY_QUERY", e)
            return EconomicActivityResponse(error = ECONOMIC_ACTIVITY_UNEXPECTED_ERROR)
        }
    }
}