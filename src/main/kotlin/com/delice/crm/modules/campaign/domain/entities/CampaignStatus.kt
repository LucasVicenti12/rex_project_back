package com.delice.crm.modules.campaign.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class CampaignStatus(override val code: Int): HasCode {
    ACTIVE(0),
    INACTIVE(1),
    WAITING(2)
}