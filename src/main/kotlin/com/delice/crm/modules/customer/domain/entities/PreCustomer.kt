package com.delice.crm.modules.customer.domain.entities

class PreCustomer (
    val companyName: String? = "",
    val tradingName: String? = "",
    val personName: String? = "",
    val document: String? = "",
    val state: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val address: String? = "",
    val complement: String? = "",
    val addressNumber: Int? = 0,
    val economicActivitiesCodes: List<String>? = listOf(),
)