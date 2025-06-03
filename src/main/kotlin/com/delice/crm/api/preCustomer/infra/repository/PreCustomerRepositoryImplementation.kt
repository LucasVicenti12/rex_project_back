package com.delice.crm.api.preCustomer.infra.repository

import com.delice.crm.core.utils.contact.Contact
import com.delice.crm.core.utils.contact.ContactType
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.api.preCustomer.domain.entities.PreCustomer
import com.delice.crm.api.preCustomer.domain.entities.PreCustomerAPIResponse
import com.delice.crm.api.preCustomer.domain.repository.PreCustomerRepository
import com.delice.crm.api.preCustomer.infra.database.PreCustomerContactsDatabase
import com.delice.crm.api.preCustomer.infra.database.PreCustomerDatabase
import com.delice.crm.api.preCustomer.infra.database.PreCustomerEconomicActivitiesDatabase
import com.delice.crm.core.utils.extensions.removeAlphaChars
import com.delice.crm.core.utils.extensions.removeSpecialChars
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import kotlin.collections.ArrayList

@Service
class PreCustomerRepositoryImplementation : PreCustomerRepository {
    override fun getPreCustomerInBase(document: String): PreCustomer? = transaction {
        val customer = PreCustomerDatabase
            .selectAll()
            .where(PreCustomerDatabase.document eq document)
            .map {
                PreCustomer(
                    uuid = it[PreCustomerDatabase.uuid],
                    companyName = it[PreCustomerDatabase.companyName],
                    tradingName = it[PreCustomerDatabase.tradingName],
                    personName = it[PreCustomerDatabase.personName],
                    document = it[PreCustomerDatabase.document],
                    state = it[PreCustomerDatabase.state],
                    zipCode = it[PreCustomerDatabase.zipCode],
                    city = it[PreCustomerDatabase.city],
                    address = it[PreCustomerDatabase.address],
                    complement = it[PreCustomerDatabase.complement],
                    addressNumber = it[PreCustomerDatabase.addressNumber],
                )
            }.firstOrNull()

        if (customer != null) {
            customer.economicActivitiesCodes = PreCustomerEconomicActivitiesDatabase
                .selectAll()
                .where(PreCustomerEconomicActivitiesDatabase.customerUUID eq customer.uuid!!)
                .map { it[PreCustomerEconomicActivitiesDatabase.code] }
        }

        if (customer != null) {
            customer.contacts = PreCustomerContactsDatabase
                .selectAll()
                .where(PreCustomerContactsDatabase.customerUUID eq customer.uuid!!)
                .map {
                    Contact(
                        uuid = it[PreCustomerContactsDatabase.uuid],
                        contactType = enumFromTypeValue<ContactType, String>(it[PreCustomerContactsDatabase.contactType]),
                        label = it[PreCustomerContactsDatabase.label],
                        isPrincipal = it[PreCustomerContactsDatabase.isPrincipal],
                    )
                }
        }

        return@transaction customer
    }

    override fun getPreCustomerInAPIBase(document: String): PreCustomer? {
        val preCustomerAPIResponse = try {
            WebClient.create("https://brasilapi.com.br")
                .get()
                .uri("/api/cnpj/v1/$document")
                .retrieve()
                .bodyToMono(PreCustomerAPIResponse::class.java)
                .block()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (preCustomerAPIResponse == null) {
            return null
        }

        val partner = preCustomerAPIResponse.qsa.get(index = 0)
        val personName = if (partner.nome_representante_legal.isNullOrEmpty()) partner.nome_socio
            ?: "" else partner.nome_representante_legal
        val numberString = preCustomerAPIResponse.numero.removeAlphaChars()

        var addressNumber = 0
        if (numberString != "") {
            addressNumber = numberString.toInt()
        }

        val contacts = ArrayList<Contact>()

        if (!preCustomerAPIResponse.ddd_telefone_1.isNullOrBlank()) {
            val phone = Contact(
                contactType = ContactType.PHONE,
                label = preCustomerAPIResponse.ddd_telefone_1,
                isPrincipal = false,
            )

            contacts.add(phone)
        }

        if (!preCustomerAPIResponse.ddd_telefone_2.isNullOrBlank()) {
            val phone = Contact(
                contactType = ContactType.PHONE,
                label = preCustomerAPIResponse.ddd_telefone_2,
                isPrincipal = false,
            )

            contacts.add(phone)
        }

        if (!preCustomerAPIResponse.email.isNullOrBlank()) {
            val email = Contact(
                contactType = ContactType.EMAIL,
                label = preCustomerAPIResponse.email,
                isPrincipal = false,
            )

            contacts.add(email)
        }

        val economicActivitiesCodes = ArrayList<String>()

        if (!preCustomerAPIResponse.cnaes_secundarios.isNullOrEmpty()) {
            preCustomerAPIResponse.cnaes_secundarios.map {
                val cnae = it.codigo
                if (cnae.isNotBlank() && cnae != "0" && cnae.length > 4) {
                    economicActivitiesCodes.add(cnae.substring(0, 5))
                }
            }
        }

        if (!preCustomerAPIResponse.cnae_fiscal.isNullOrBlank()) {
            val cnae = preCustomerAPIResponse.cnae_fiscal
            if (cnae.isNotBlank() && cnae != "0" && cnae.length > 4) {
                economicActivitiesCodes.add(preCustomerAPIResponse.cnae_fiscal.substring(0, 5))
            }
        }

        return PreCustomer(
            companyName = preCustomerAPIResponse.razao_social,
            tradingName = preCustomerAPIResponse.nome_fantasia,
            personName = personName,
            document = preCustomerAPIResponse.cnpj,
            state = preCustomerAPIResponse.uf,
            zipCode = preCustomerAPIResponse.cep,
            city = preCustomerAPIResponse.municipio,
            address = preCustomerAPIResponse.logradouro,
            complement = preCustomerAPIResponse.complemento,
            addressNumber = addressNumber,
            economicActivitiesCodes = economicActivitiesCodes,
            contacts = contacts,
        )
    }

    override fun savePreCustomer(preCustomer: PreCustomer) {
        transaction {
            val customerUUID = UUID.randomUUID()

            var tempComplement = ""

            tempComplement = if (preCustomer.complement!!.length > 60) {
                preCustomer.complement!!.substring(0, 60)
            } else {
                preCustomer.complement!!
            }

            PreCustomerDatabase.insert {
                it[uuid] = customerUUID
                it[companyName] = preCustomer.companyName!!
                it[tradingName] = preCustomer.tradingName!!
                it[personName] = preCustomer.personName!!
                it[document] = preCustomer.document!!
                it[state] = preCustomer.state!!
                it[city] = preCustomer.city!!
                it[address] = preCustomer.address!!
                it[zipCode] = preCustomer.zipCode!!
                it[complement] = tempComplement
                it[addressNumber] = preCustomer.addressNumber!!
            }

            if (!preCustomer.economicActivitiesCodes.isNullOrEmpty()) {
                preCustomer.economicActivitiesCodes!!.forEach { economicActivity ->
                    PreCustomerEconomicActivitiesDatabase.insert {
                        it[uuid] = UUID.randomUUID()
                        it[PreCustomerEconomicActivitiesDatabase.customerUUID] = customerUUID
                        it[code] = economicActivity
                    }
                }
            }

            if (!preCustomer.contacts.isNullOrEmpty()) {
                preCustomer.contacts!!.forEach { contact ->
                    PreCustomerContactsDatabase.insert {
                        it[uuid] = UUID.randomUUID()
                        it[contactType] = contact.contactType!!.type
                        it[label] = contact.label!!
                        it[isPrincipal] = contact.isPrincipal
                        it[PreCustomerContactsDatabase.customerUUID] = customerUUID
                    }
                }
            }
        }
    }
}