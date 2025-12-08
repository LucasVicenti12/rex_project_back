package com.delice.crm.modules.campaign.domain.repository

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignMetadata
import java.util.*

interface CampaignRepository {
    fun createCampaign(campaign: Campaign): Campaign?

    fun updateCampaign(campaign: Campaign): Campaign?

    fun getCampaignByUUID(campaignUUID: UUID): Campaign?

    fun getAllSaleCampaign(): List<Campaign>?

    fun getCampaignPagination(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): Pagination<Campaign>?

    fun saveCampaignMetadata(
        campaignUUID: UUID,
        metadata: CampaignMetadata?,
        modifiedBy: UUID,
    ): Campaign?

    fun getVisitCampaign(uuid: UUID): Campaign?
}