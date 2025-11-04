package com.delice.crm.modules.campaign.domain.usecase.implementation

import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignStatus
import com.delice.crm.modules.campaign.domain.exceptions.*
import com.delice.crm.modules.campaign.domain.repository.CampaignRepository
import com.delice.crm.modules.campaign.domain.usecase.CampaignUseCase
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignResponse
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignPaginationResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CampaignUseCaseImplementation(
    private val campaignRepository: CampaignRepository,
) : CampaignUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(CampaignUseCaseImplementation::class.java)
    }

    override fun createCampaign(campaign: Campaign): CampaignResponse = try {
        val user = getCurrentUser()

        campaign.createdBy = user.getUserData()
        campaign.modifiedBy = user.getUserData()
        campaign.status = CampaignStatus.FORM_PENDING

        validateCampaign(campaign).let {
            if (it != null) {
                CampaignResponse(error = it)
            } else {
                CampaignResponse(campaign = campaignRepository.createCampaign(campaign))
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_ON_CREATE_CAMPAIGN", e)
        CampaignResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
    }

    override fun updateCampaign(campaign: Campaign): CampaignResponse = try {
        val user = getCurrentUser()

        campaign.modifiedBy = user.getUserData()

        validateCampaign(campaign).let {
            if (it != null) {
                CampaignResponse(error = it)
            } else {
                val exists = campaignRepository.getCampaignByUUID(campaign.uuid!!)

                if (exists == null) {
                    CampaignResponse(error = CAMPAIGN_NOT_FOUND)
                } else {
                    CampaignResponse(campaign = campaignRepository.updateCampaign(campaign))
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_ON_UPDATE_CAMPAIGN", e)
        CampaignResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
    }

    override fun getCampaignByUUID(campaignUUID: UUID): CampaignResponse = try {
        val campaign = campaignRepository.getCampaignByUUID(campaignUUID)

        if (campaign == null) {
            CampaignResponse(error = CAMPAIGN_NOT_FOUND)
        } else {
            CampaignResponse(campaign = campaign)
        }
    } catch (e: Exception) {
        logger.error("ERROR_ON_GET_CAMPAIGN_BY_UUID", e)
        CampaignResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
    }

    override fun getCampaignPagination(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): CampaignPaginationResponse {
        return try {
            return CampaignPaginationResponse(
                campaigns = campaignRepository.getCampaignPagination(page, count, orderBy, params),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ERROR_ON_GET_CAMPAIGN_PAGINATION", e)
            CampaignPaginationResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
        }
    }

    private fun validateCampaign(campaign: Campaign): CampaignException? = when {
        campaign.title.isNullOrBlank() -> {
            CAMPAIGN_TITLE_IS_EMPTY
        }

        campaign.objective.isNullOrBlank() -> {
            CAMPAIGN_OBJECTIVE_IS_EMPTY
        }

        else -> {
            null
        }
    }
}

