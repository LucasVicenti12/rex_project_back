package com.delice.crm.api.economicActivities.domain.entities

import java.util.UUID

class EconomicActivity (
    val uuid: UUID,
    val code: String,
    val description: String,
    val group: EconomicActivityAttribute,
    val division: EconomicActivityAttribute,
    val section: EconomicActivityAttribute,
)

class EconomicActivityAttribute(
    val code: String,
    val description: String,
)

class EconomicActivityAPIResponse(
    val id: String,
    val descricao: String,
    val grupo: EconomicActivityGroupAPIResponse,
)

class EconomicActivityGroupAPIResponse(
    val id: String,
    val descricao: String,
    val divisao: EconomicActivityDivisionAPIResponse,
)

class EconomicActivityDivisionAPIResponse(
    val id: String,
    val descricao: String,
    val secao: EconomicActivitySectionAPIResponse,
)

class EconomicActivitySectionAPIResponse(
    val id: String,
    val descricao: String,
)