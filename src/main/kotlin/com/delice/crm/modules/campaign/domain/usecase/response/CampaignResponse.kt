package com.delice.crm.modules.campaign.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignMedia
import com.delice.crm.modules.campaign.domain.exceptions.CampaignExceptions
import com.delice.crm.modules.product.domain.entities.SimpleProduct

data class CampaignResponse(
    val campaign: Campaign? = null,
    val error: CampaignExceptions? = null
)

data class CampaignPaginationResponse(
    val campaigns: Pagination<Campaign>? = null,
    val error: CampaignExceptions? = null
)

data class CampaignMediaResponse(
    val media: List<CampaignMedia>? = emptyList(),
    val error: CampaignExceptions? = null
)

data class FreeProducts(
    val products: List<SimpleProduct>? = null,
    val error: CampaignExceptions? = null
)

