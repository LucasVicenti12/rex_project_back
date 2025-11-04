package com.delice.crm.modules.campaign.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.exceptions.CampaignException

data class CampaignResponse(
    val campaign: Campaign? = null,
    val error: CampaignException? = null
)

data class CampaignPaginationResponse(
    val campaigns: Pagination<Campaign>? = null,
    val error: CampaignException? = null
)
