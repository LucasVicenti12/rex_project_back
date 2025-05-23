package com.delice.crm.modules.customer.domain.entities

import com.delice.crm.core.utils.contact.Contact
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import java.time.LocalDateTime
import java.util.UUID

class Customer(
    val uuid: UUID? = null,
    val companyName: String? = "",
    val tradingName: String? = "",
    val personName: String? = "",
    val document: String? = "",
    val contacts: List<Contact>? = listOf(),
    val state: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val address: String? = "",
    val complement: String? = "",
    val addressNumber: Int? = 0,
    val economicActivitiesCodes: List<String>? = listOf(),
    val economicActivities: List<EconomicActivity>? = listOf(),
    val observation: String? = "",
    val status: CustomerStatus? = CustomerStatus.PENDING,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
    val createdBy: UUID? = null,
    val modifiedBy: UUID? = null,
)