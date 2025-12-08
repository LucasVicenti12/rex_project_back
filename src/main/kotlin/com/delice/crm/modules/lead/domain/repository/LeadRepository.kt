package com.delice.crm.modules.lead.domain.repository

import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.lead.domain.entities.Lead
import java.util.UUID

interface LeadRepository {
    fun saveLead(lead: Lead): Lead?
    fun approveLead(lead: Lead, userUUID: UUID)
    fun rejectLead(leadUUID: UUID)
    fun getLeadByUUID(uuid: UUID): Lead?
    fun getLeadByDocument(document: String): Lead?
    fun getPaginatedLead(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): Pagination<Lead>?
}