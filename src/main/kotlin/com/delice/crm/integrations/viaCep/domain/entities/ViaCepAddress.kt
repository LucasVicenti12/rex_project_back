package com.delice.crm.integrations.viaCep.domain.entities

class ViaCepAddress (
    zipCode: Int? = 0,
    address: String? = "",
    city: String? = "",
    state: String? = ""
)

class ViaCepResponse (
    cep: String? = "",
    logradouro: String? = "",
    bairro: String? = "",
    localidade: String? = "",
    uf: String? = ""
)