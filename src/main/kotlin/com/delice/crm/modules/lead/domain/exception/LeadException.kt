package com.delice.crm.modules.lead.domain.exception

import com.delice.crm.core.utils.exception.DefaultError

val LEAD_UNEXPECTED = LeadException("LEAD_UNEXPECTED", "An unexpected error has occurred")
val LEAD_DOCUMENT_IS_EMPTY = LeadException("LEAD_DOCUMENT_IS_EMPTY", "The document is empty")
val LEAD_EMAIL_IS_EMPTY = LeadException("LEAD_EMAIL_IS_EMPTY", "The email is empty")
val LEAD_ALREADY_EXISTS = LeadException("LEAD_ALREADY_EXISTS", "A lead already exists with this document")
val LEAD_NOT_FOUND = LeadException("LEAD_NOT_FOUND", "The lead not found")

class LeadException(code: String, description: String): DefaultError(code, description)