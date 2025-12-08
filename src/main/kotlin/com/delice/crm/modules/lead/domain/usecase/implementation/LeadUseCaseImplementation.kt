package com.delice.crm.modules.lead.domain.usecase.implementation

import com.delice.crm.api.preCustomer.domain.entities.PreCustomer
import com.delice.crm.api.preCustomer.domain.repository.PreCustomerRepository
import com.delice.crm.api.preCustomer.domain.usecase.PreCustomerUseCase
import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.queue.MailQueue
import com.delice.crm.core.utils.contact.ContactType
import com.delice.crm.core.utils.extensions.removeAlphaChars
import com.delice.crm.core.utils.extensions.removeSpecialChars
import com.delice.crm.core.utils.formatter.DateTimeFormat
import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.lead.domain.entities.LEAD_APPROVAL_TITLE
import com.delice.crm.modules.lead.domain.entities.LEAD_CONTENT_EMAIL
import com.delice.crm.modules.lead.domain.entities.Lead
import com.delice.crm.modules.lead.domain.exception.*
import com.delice.crm.modules.lead.domain.repository.LeadRepository
import com.delice.crm.modules.lead.domain.usecase.LeadUseCase
import com.delice.crm.modules.lead.domain.usecase.response.LeadApprovalResponse
import com.delice.crm.modules.lead.domain.usecase.response.LeadPaginationResponse
import com.delice.crm.modules.lead.domain.usecase.response.LeadResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class LeadUseCaseImplementation(
    private val leadRepository: LeadRepository,
    private val preCustomerUseCase: PreCustomerUseCase,
    private val mailQueue: MailQueue,
) : LeadUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(LeadUseCaseImplementation::class.java)
    }

    override fun saveLead(lead: Lead): LeadResponse {
        try {
            val document = lead.document?.removeSpecialChars().orEmpty()

            if (document.isEmpty()) return LeadResponse(error = LEAD_DOCUMENT_IS_EMPTY)

            lead.document = document
            lead.phone = lead.phone.orEmpty().removeAlphaChars().removeSpecialChars()

            if (leadRepository.getLeadByDocument(document) != null) {
                return LeadResponse(error = LEAD_ALREADY_EXISTS)
            }

            if (lead.email.isNullOrBlank()) {
                return LeadResponse(error = LEAD_EMAIL_IS_EMPTY)
            }

            val preCustomer = preCustomerUseCase.getPreCustomer(document)
            val customer = preCustomer.customer

            if (customer != null) {
                mergeCustomerIntoLead(lead, customer)
            }

            val savedLead = leadRepository.saveLead(lead)
            return LeadResponse(lead = savedLead)

        } catch (e: Exception) {
            logger.error("ERROR_ON_SAVE_LEAD", e)
            return LeadResponse(error = LEAD_UNEXPECTED)
        }
    }

    private fun <T> mergeField(
        leadValue: T?,
        customerValue: T?,
        defaultEmpty: T
    ): T = when (leadValue) {
        null -> customerValue ?: defaultEmpty
        is String -> leadValue.ifBlank { customerValue ?: defaultEmpty }
        is Number -> if (leadValue.toDouble() != 0.0) leadValue else customerValue ?: defaultEmpty
        else -> leadValue
    }

    private fun mergeCustomerIntoLead(lead: Lead, customer: PreCustomer) {
        val phone = customer.contacts?.find { it.contactType == ContactType.PHONE }?.label
        val email = customer.contacts?.find { it.contactType == ContactType.EMAIL }?.label
        val econ = customer.economicActivitiesCodes?.firstOrNull()

        lead.companyName = mergeField(lead.companyName, customer.companyName, "")
        lead.tradingName = mergeField(lead.tradingName, customer.tradingName, "")
        lead.personName = mergeField(lead.personName, customer.personName, "")
        lead.email = mergeField(lead.email, email, "")
        lead.phone = mergeField(lead.phone, phone, "")
        lead.state = mergeField(lead.state, customer.state, "")
        lead.city = mergeField(lead.city, customer.city, "")
        lead.zipCode = mergeField(lead.zipCode, customer.zipCode, "")
        lead.address = mergeField(lead.address, customer.address, "")
        lead.complement = mergeField(lead.complement, customer.complement, "")
        lead.addressNumber = mergeField(lead.addressNumber, customer.addressNumber, 0)
        lead.economicActivity = mergeField(lead.economicActivity, econ, "")
    }

    override fun approveLead(uuid: UUID): LeadApprovalResponse {
        try {
            val currentUser = getCurrentUser()

            val lead = leadRepository.getLeadByUUID(uuid) ?: return LeadApprovalResponse(error = LEAD_NOT_FOUND)

            leadRepository.approveLead(lead, currentUser.uuid)

            val date = LocalDateTime.now().format(DateTimeFormat)

            val mail = Mail(
                subject = LEAD_APPROVAL_TITLE,
                content = LEAD_CONTENT_EMAIL.format(
                    lead.document!!,
                    lead.companyName!!,
                    "Approved",
                    date,
                ),
                to = lead.email!!,
                withHtml = true
            )

            mailQueue.addMail(mail)

            return LeadApprovalResponse(message = "LEAD_CREATED_WITH_SUCCESS")
        } catch (e: Exception) {
            logger.error("ERROR_ON_APPROVE_LEAD", e)
            return LeadApprovalResponse(error = LEAD_UNEXPECTED)
        }
    }

    override fun rejectLead(uuid: UUID): LeadApprovalResponse {
        try {
            leadRepository.rejectLead(uuid)

            val lead = leadRepository.getLeadByUUID(uuid)!!

            val date = LocalDateTime.now().format(DateTimeFormat)

            val mail = Mail(
                subject = LEAD_APPROVAL_TITLE,
                content = LEAD_CONTENT_EMAIL.format(
                    lead.document!!,
                    lead.companyName!!,
                    "Reproved",
                    date,
                ),
                to = lead.email!!,
                withHtml = true
            )

            mailQueue.addMail(mail)

            return LeadApprovalResponse(message = "LEAD_CREATED_WITH_SUCCESS")
        } catch (e: Exception) {
            logger.error("ERROR_ON_REPROVE_LEAD", e)
            return LeadApprovalResponse(error = LEAD_UNEXPECTED)
        }
    }

    override fun getLeadByUUID(uuid: UUID): LeadResponse = try {
        LeadResponse(leadRepository.getLeadByUUID(uuid))
    } catch (e: Exception) {
        logger.error("ERROR_ON_GET_LEAD_BY_UUID", e)
        LeadResponse(error = LEAD_UNEXPECTED)
    }

    override fun getPaginatedLead(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): LeadPaginationResponse = try {
        LeadPaginationResponse(
            leads = leadRepository.getPaginatedLead(
                page = page,
                count = count,
                orderBy = orderBy,
                params = params
            )
        )
    } catch (e: Exception) {
        logger.error("ERROR_ON_GET_LEAD_PAGINATION", e)
        LeadPaginationResponse(error = LEAD_UNEXPECTED)
    }
}