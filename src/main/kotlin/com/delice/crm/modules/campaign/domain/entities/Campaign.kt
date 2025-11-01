package com.delice.crm.modules.campaign.domain.entities

import com.delice.crm.modules.product.domain.entities.Product
import java.time.LocalDate
import java.util.UUID

class Campaign(
    val uuid: UUID? = null,
    val title: String? = null,
    val description: String? = null,
    val status: CampaignStatus? = CampaignStatus.ACTIVE,
    val type: String? = null,
    val channel: String? = null,
    var accountable: UUID? = null,
    var products: List<Product>? = emptyList(),
    val startDate: LocalDate? = LocalDate.now(),
    val endDate: LocalDate? = LocalDate.now(),
    val objective: Double? = null,
    val createdAt: LocalDate? = LocalDate.now(),
    val modifiedAt: LocalDate? = LocalDate.now()
)


class CampaignMedia(
    val uuid: UUID? = null,
    val campaignUUID: UUID,
    val image: String? = null,
    val isPrincipal: Boolean? = false,
    val viewIndex: Int? = 0,
    val createdAt: LocalDate? = null,
    val modifiedAt: LocalDate? = null
)