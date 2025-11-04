package com.delice.crm.modules.lead.domain.usecase

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.lead.domain.entities.Lead
import com.delice.crm.modules.lead.domain.usecase.response.LeadApprovalResponse
import com.delice.crm.modules.lead.domain.usecase.response.LeadPaginationResponse
import com.delice.crm.modules.lead.domain.usecase.response.LeadResponse
import java.util.*

interface LeadUseCase {
    fun saveLead(lead: Lead): LeadResponse
    fun approveLead(uuid: UUID): LeadApprovalResponse
    fun rejectLead(uuid: UUID): LeadApprovalResponse
    fun getLeadByUUID(uuid: UUID): LeadResponse
    fun getPaginatedLead(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): LeadPaginationResponse
}