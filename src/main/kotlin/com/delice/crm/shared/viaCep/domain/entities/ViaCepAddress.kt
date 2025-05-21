package com.delice.crm.shared.viaCep.domain.entities

class ViaCepAddress (
    val zipCode: String? = "",
    val address: String? = "",
    val district: String? = "",
    val city: String? = "",
    val state: String? = ""
)

class ViaCepResponse (
    val logradouro: String,
    val bairro: String,
    val localidade: String,
    val uf: String
)