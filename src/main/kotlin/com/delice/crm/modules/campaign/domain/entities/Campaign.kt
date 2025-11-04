package com.delice.crm.modules.campaign.domain.entities

import com.delice.crm.core.user.domain.entities.User
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class Campaign(
    val uuid: UUID? = null,
    val title: String? = null,
    val description: String? = "",
    val objective: String? = "",
    var status: CampaignStatus? = CampaignStatus.FORM_PENDING,
    val type: CampaignType? = CampaignType.SALE,
    val metadata: CampaignMetadata? = null,
    var createdBy: User? = null,
    var modifiedBy: User? = null,
    val start: LocalDateTime? = LocalDateTime.now(),
    val end: LocalDateTime? = LocalDateTime.now(),
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now()
)

@Serializable
data class CampaignMetadata(
    val products: List<DiscountedProduct>? = null,
    val salesTarget: Double? = null,
    val campaignLeadFields: List<CampaignLeadFields>? = null
)

@Serializable
data class DiscountedProduct(
    val product: String,
    val discount: Double
)

@Serializable
data class CampaignLeadFields(
    val type: CampaignLeadFieldType,
    val active: Boolean
)