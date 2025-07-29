package com.delice.crm.modules.customer.domain.entities

import com.delice.crm.core.utils.contact.Contact
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

class Customer(
    var uuid: UUID? = null,
    val companyName: String? = "",
    val tradingName: String? = "",
    val personName: String? = "",
    val document: String? = "",
    var contacts: List<Contact>? = listOf(),
    val state: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val address: String? = "",
    val complement: String? = "",
    val addressNumber: Int? = 0,
    val economicActivitiesCodes: List<String>? = listOf(),
    var economicActivities: List<EconomicActivity>? = listOf(),
    val observation: String? = "",
    val status: CustomerStatus? = CustomerStatus.PENDING,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
    val createdBy: UUID? = null,
    val modifiedBy: UUID? = null,
)

data class SimpleCustomer(
    val uuid: UUID,
    val companyName: String,
    val document: String,
)

@Serializable
data class SerializableCustomer(
    var uuid: String? = null,
    var companyName: String? = null,
)