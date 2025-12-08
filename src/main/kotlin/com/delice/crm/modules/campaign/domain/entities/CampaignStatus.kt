package com.delice.crm.modules.campaign.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class CampaignStatus(override val code: Int): HasCode {
    ACTIVE(0),
    INACTIVE(1),
    FORM_PENDING(2)
}

enum class CampaignType(override val code: Int): HasCode {
    SALE(0),
    LEAD(1)
}