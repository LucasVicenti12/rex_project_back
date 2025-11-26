package com.delice.crm.modules.campaign.domain.entities

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.modules.product.domain.entities.SerializableProduct
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
    var products: List<DiscountedProduct>? = null,
    var salesTarget: Double? = null,
    var campaignLeadFields: List<CampaignLeadFields>? = null
)

@Serializable
data class DiscountedProduct(
    val product: SerializableProduct,
    val discount: Double
)

@Serializable
data class CampaignLeadFields(
    val type: String,
    val active: Boolean
)