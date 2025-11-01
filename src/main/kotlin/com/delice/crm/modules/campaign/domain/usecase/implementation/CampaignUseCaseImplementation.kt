package com.delice.crm.modules.campaign.domain.usecase.implementation

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.exceptions.CAMPAIGN_NOT_FOUND
import com.delice.crm.modules.campaign.domain.exceptions.CAMPAIGN_UNEXPECTED_ERROR
import com.delice.crm.modules.campaign.domain.repository.CampaignRepository
import com.delice.crm.modules.campaign.domain.usecase.CampaignUseCase
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignResponse
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignPaginationResponse
import com.delice.crm.modules.campaign.domain.entities.CampaignMedia
import com.delice.crm.modules.campaign.domain.exceptions.CAMPAIGN_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL
import com.delice.crm.modules.campaign.domain.exceptions.CAMPAIGN_MEDIA_MUST_BE_PRINCIPAL
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignMediaResponse
import com.delice.crm.modules.campaign.domain.usecase.response.FreeProducts
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CampaignUseCaseImplementation (
    private val campaignRepository: CampaignRepository,
) : CampaignUseCase {
    companion object{
        private val logger = LoggerFactory.getLogger(CampaignUseCaseImplementation::class.java)
    }

    override fun createCampaign(campaign: Campaign): CampaignResponse = try {
        val validate = validateCampaign(campaign)

        when {

            validate.error != null -> {
                validate
            }

            else -> {
                CampaignResponse(campaign = campaignRepository.createCampaign(campaign))
            }
        }
    } catch (e: Exception) {
        logger.error("CREATE_CAMPAIGN", e)
        CampaignResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
    }

    override fun updateCampaign(campaign: Campaign): CampaignResponse = try {
        val validate = validateCampaign(campaign)

        when {
            validate.error != null -> {
                validate
            }

            campaign.uuid == null -> {
                CampaignResponse(error = CAMPAIGN_NOT_FOUND)
            }

            campaignRepository.getCampaignByUUID(campaign.uuid) == null -> {
                CampaignResponse(error = CAMPAIGN_NOT_FOUND)
            }

            else -> {
                CampaignResponse(campaign = campaignRepository.updateCampaign(campaign))
            }
        }
    } catch (e: Exception) {
        logger.error("UPDATE_CAMPAIGN", e)
        CampaignResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
    }

    override fun saveCampaignMedia(media: List<CampaignMedia>, campaignUUID: UUID): CampaignMediaResponse {
        try {
            campaignRepository.getCampaignByUUID(campaignUUID) ?: return CampaignMediaResponse(error = CAMPAIGN_NOT_FOUND)

            val hasMorePrincipal = media.count { it.isPrincipal == true }

            if (hasMorePrincipal == 0) return CampaignMediaResponse(error = CAMPAIGN_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL)

            if (hasMorePrincipal > 1) return CampaignMediaResponse(error = CAMPAIGN_MEDIA_MUST_BE_PRINCIPAL)

            campaignRepository.saveCampaignMedia(media, campaignUUID)
            return CampaignMediaResponse(media = media)
        } catch (e: Exception) {
            CampaignUseCaseImplementation.logger.error("SAVE_CAMPAIGN_MEDIA", e)
            return CampaignMediaResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
        }
    }

    override fun getCampaignByUUID(campaignUUID: UUID): CampaignResponse = try {
        val campaign = campaignRepository.getCampaignByUUID(campaignUUID)

        if (campaign == null) {
            CampaignResponse(error = CAMPAIGN_NOT_FOUND)
        } else {
            CampaignResponse(campaign = campaign)
        }
    } catch (e: Exception) {
        logger.error("GET_CAMPAIGN_BY_UUID", e)
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
            logger.error("GET_CAMPAIGN_PAGINATION", e)
            CampaignPaginationResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
        }
    }

    override fun getFreeProducts(): FreeProducts {
        return try {
            return FreeProducts(
                products = campaignRepository.getFreeProducts(),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ERROR_GET_FREE_PRODUCT", e)
            FreeProducts(error = CAMPAIGN_UNEXPECTED_ERROR)
        }
    }

    private fun validateCampaign(campaign: Campaign): CampaignResponse = when {
        campaign.title?.isBlank() == true -> {
            CampaignResponse(error = CAMPAIGN_UNEXPECTED_ERROR)
        }

        else -> {
            CampaignResponse()
        }
    }
}

