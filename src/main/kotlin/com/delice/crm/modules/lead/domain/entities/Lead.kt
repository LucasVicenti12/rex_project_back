package com.delice.crm.modules.lead.domain.entities

import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.core.utils.contact.Contact
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import java.time.LocalDateTime
import java.util.UUID

class Lead(
    val uuid: UUID? = null,
    val document: String? = "",
    val companyName: String? = "",
    val tradingName: String? = "",
    val personName: String? = "",
    val email: String? = "",
    var contacts: List<Contact>? = listOf(),
    val state: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val address: String? = "",
    val complement: String? = "",
    val addressNumber: Int? = 0,
    val status: LeadStatus? = LeadStatus.PENDING,
    val economicActivitiesCodes: List<String>? = listOf(),
    var economicActivities: List<EconomicActivity>? = listOf(),
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
) {
    fun toCustomer(status: CustomerStatus): Customer = Customer(
        companyName = this.companyName,
        tradingName = this.tradingName,
        personName = this.personName,
        document = this.document,
        contacts = this.contacts,
        state = this.state,
        city = this.city,
        zipCode = this.zipCode,
        address = this.address,
        complement = this.complement,
        addressNumber = this.addressNumber,
        economicActivitiesCodes = this.economicActivitiesCodes,
        economicActivities = this.economicActivities,
        observation = "",
        status = status,
        createdAt = LocalDateTime.now(),
        modifiedAt = LocalDateTime.now(),
    )
}

const val LEAD_APPROVAL_TITLE = "Your pre register is approved with success!"
const val LEAD_REPROVED_TITLE = "Your pre register is reproved!"
const val LEAD_CONTENT_EMAIL = """
    <table cellpadding="0" cellspacing="0" border="0" width="250" style="font-family: Arial, sans-serif; background-color: #dbe7f0; border-radius: 6px; padding: 10px;">
        <tr>
            <td style="font-size: 16px; font-weight: bold; color: #333333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                %s %s
            </td>
        </tr>
        <tr>
            <td style="font-size: 12px; color: #666666; padding-top: 8px;">
                %s on: %s
            </td>
        </tr>
    </table>
"""