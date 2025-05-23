package com.delice.crm.api.preCustomer.domain.entities

import com.delice.crm.core.utils.contact.Contact
import java.util.UUID

class PreCustomer(
    val uuid: UUID? = null,
    val companyName: String? = "",
    val tradingName: String? = "",
    val personName: String? = "",
    val document: String? = "",
    val state: String? = "",
    val zipCode: String? = "",
    val city: String? = "",
    val address: String? = "",
    val complement: String? = "",
    val addressNumber: Int? = 0,
    var economicActivitiesCodes: List<String>? = listOf(),
    var contacts: List<Contact>? = listOf(),
)

class PreCustomerAPIResponse(
    val razao_social: String,
    val nome_fantasia: String,
    val qsa: List<PartnersAdministrators>,
    val cnpj: String,
    val cep: String,
    val uf: String,
    val municipio: String,
    val logradouro: String,
    val complemento: String,
    val numero: String,
    val codigo_porte: Int,
    val ddd_telefone_1: String? = "",
    val ddd_telefone_2: String? = "",
    val email: String? = "",
    val cnae_fiscal: String? = "",
    val cnaes_secundarios: List<EconomicActivitiesPreCustomerAPIResponse>? = listOf(),
    val situacao_cadastral: Int
)

class PartnersAdministrators(
    val nome_socio: String? = null,
    val nome_representante_legal: String? = null,
)

class EconomicActivitiesPreCustomerAPIResponse(
    val codigo: String,
)