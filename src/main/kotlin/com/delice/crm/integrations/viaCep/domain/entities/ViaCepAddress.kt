package com.delice.crm.integrations.viaCep.domain.entities

class ViaCepAddress (
    val zipCode: String? = "",
    val address: String? = "",
    val city: String? = "",
    val state: String? = ""
)

class ViaCepResponse (
    val logradouro: String,
    val bairro: String,
    val localidade: String,
    val uf: String
)