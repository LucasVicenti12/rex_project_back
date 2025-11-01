package com.delice.crm.modules.campaign.domain.repository

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignMedia
import com.delice.crm.modules.product.domain.entities.SimpleProduct
import java.util.*

interface CampaignRepository {
    fun createCampaign(campaign: Campaign): Campaign?
    fun updateCampaign(campaign: Campaign): Campaign?
    fun getCampaignByUUID(campaign: UUID): Campaign?
    fun getCampaignPagination(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): Pagination<Campaign>?
    fun saveCampaignMedia(media: List<CampaignMedia>, campaignUUID: UUID): List<CampaignMedia>
    fun getProductCampaign(productUUID: UUID, campaignUUID: UUID?): Campaign?
    fun getFreeProducts(): List<SimpleProduct>?
}