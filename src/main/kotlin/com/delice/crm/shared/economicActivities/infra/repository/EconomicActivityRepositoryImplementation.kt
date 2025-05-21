package com.delice.crm.shared.economicActivities.infra.repository

import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivityAPIResponse
import com.delice.crm.shared.economicActivities.domain.entities.EconomicActivityAttribute
import com.delice.crm.shared.economicActivities.domain.repository.EconomicActivityRepository
import com.delice.crm.shared.economicActivities.infra.database.EconomicActivityDatabase
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Service
class EconomicActivityRepositoryImplementation : EconomicActivityRepository {
    override fun getEconomicActivityInBase(code: String): EconomicActivity? = transaction {
        EconomicActivityDatabase.selectAll().where(
            EconomicActivityDatabase.code eq code
        ).map {
            EconomicActivity(
                code = it[EconomicActivityDatabase.code],
                description = it[EconomicActivityDatabase.description],
                group = EconomicActivityAttribute(
                    code = it[EconomicActivityDatabase.groupCode],
                    description = it[EconomicActivityDatabase.groupDescription],
                ),
                division = EconomicActivityAttribute(
                    code = it[EconomicActivityDatabase.divisionCode],
                    description = it[EconomicActivityDatabase.divisionDescription],
                ),
                section = EconomicActivityAttribute(
                    code = it[EconomicActivityDatabase.sectionCode],
                    description = it[EconomicActivityDatabase.sectionDescription],
                ),
            )
        }.firstOrNull()
    }

    override fun getEconomicActivityInAPIBase(code: String): EconomicActivity? {
        val economicActivityResponse = try {
            WebClient.create("https://servicodados.ibge.gov.br")
                .get()
                .uri("/api/v2/cnae/classes/$code")
                .retrieve()
                .bodyToMono(EconomicActivityAPIResponse::class.java)
                .block()
        } catch (e: Exception) {
            null
        }

        if (economicActivityResponse == null) {
            return null
        }

        return EconomicActivity(
            code = code,
            description = economicActivityResponse.descricao,
            group = EconomicActivityAttribute(
                code = economicActivityResponse.grupo.id,
                description = economicActivityResponse.grupo.descricao
            ),
            division = EconomicActivityAttribute(
                code = economicActivityResponse.grupo.divisao.id,
                description = economicActivityResponse.grupo.divisao.descricao
            ),
            section = EconomicActivityAttribute(
                code = economicActivityResponse.grupo.divisao.secao.id,
                description = economicActivityResponse.grupo.divisao.secao.descricao
            )
        )
    }

    override fun saveEconomicActivityInBase(activity: EconomicActivity) {
        transaction {
            EconomicActivityDatabase.insert {
                it[uuid] = UUID.randomUUID()
                it[code] = activity.code
                it[description] = activity.description
                it[groupCode] = activity.group.code
                it[groupDescription] = activity.group.description
                it[divisionCode] = activity.division.code
                it[divisionDescription] = activity.division.description
                it[sectionCode] = activity.section.code
                it[sectionDescription] = activity.section.description
            }
        }
    }
}