package com.delice.crm.shared.viaCep.infra.repository

import com.delice.crm.shared.viaCep.domain.entities.ViaCepAddress
import com.delice.crm.shared.viaCep.domain.entities.ViaCepResponse
import com.delice.crm.shared.viaCep.domain.repository.ViaCepRepository
import com.delice.crm.shared.viaCep.infra.database.AddressDatabase
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class ViaCepRepositoryImplementation : ViaCepRepository {
    override fun getAddressInBase(zipCode: String): ViaCepAddress? = transaction {
        AddressDatabase.selectAll()
            .where(
                AddressDatabase.zipCode eq zipCode
            ).map {
                ViaCepAddress(
                    zipCode = it[AddressDatabase.zipCode],
                    address = it[AddressDatabase.address],
                    district = it[AddressDatabase.district],
                    city = it[AddressDatabase.city],
                    state = it[AddressDatabase.state],
                )
            }.firstOrNull()
    }

    override fun getAddressInViaCepBase(zipCode: String): ViaCepAddress? {
        val viaCepResponse = try {
            WebClient.create("https://viacep.com.br")
                .get()
                .uri("/ws/$zipCode/json/")
                .retrieve()
                .bodyToMono(ViaCepResponse::class.java).block()
        } catch (ex: Exception) {
            null
        }

        if (viaCepResponse == null) {
            return null
        }

        return ViaCepAddress(
            zipCode = zipCode,
            address = viaCepResponse.logradouro,
            district = viaCepResponse.bairro,
            city = viaCepResponse.localidade,
            state = viaCepResponse.uf,
        )
    }

    override fun saveAddressInBase(address: ViaCepAddress) {
        transaction {
            AddressDatabase.insert {
                it[uuid] = UUID.randomUUID()
                it[zipCode] = address.zipCode!!
                it[city] = address.city!!
                it[state] = address.state!!
                it[district] = address.district!!
                it[AddressDatabase.address] = address.address!!
            }
        }
    }
}