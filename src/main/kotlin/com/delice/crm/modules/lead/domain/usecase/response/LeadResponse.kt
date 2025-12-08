package com.delice.crm.modules.lead.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.lead.domain.entities.Lead
import com.delice.crm.modules.lead.domain.exception.LeadException

class LeadResponse (
    val lead: Lead? = null,
    val error: LeadException? = null
)

class LeadPaginationResponse(
    val leads: Pagination<Lead>? = null,
    val error: LeadException? = null
)

class LeadApprovalResponse(
    val message: String? = null,
    val error: LeadException? = null
)