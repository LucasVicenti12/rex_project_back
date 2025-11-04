package com.delice.crm.modules.campaign.domain.usecase

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignPaginationResponse
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignResponse
import java.util.UUID

interface CampaignUseCase {
    fun createCampaign(campaign: Campaign): CampaignResponse

    fun updateCampaign(campaign: Campaign): CampaignResponse

    fun getCampaignByUUID(campaignUUID: UUID): CampaignResponse

    fun getCampaignPagination(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): CampaignPaginationResponse
}