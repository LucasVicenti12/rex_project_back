package com.delice.crm.modules.lead.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class LeadStatus(override val code: Int) : HasCode {
    PENDING(0),
    APPROVED(1),
    REPROVED(2)
}