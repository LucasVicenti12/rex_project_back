package com.delice.crm.modules.lead.infra.repository

import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivityAttribute
import com.delice.crm.api.economicActivities.infra.database.EconomicActivityDatabase
import com.delice.crm.core.utils.contact.Contact
import com.delice.crm.core.utils.contact.ContactType
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.lead.domain.entities.Lead
import com.delice.crm.modules.lead.domain.entities.LeadStatus
import com.delice.crm.modules.lead.domain.repository.LeadRepository
import com.delice.crm.modules.lead.infra.database.LeadContactsDatabase
import com.delice.crm.modules.lead.infra.database.LeadDatabase
import com.delice.crm.modules.lead.infra.database.LeadEconomicActivitiesDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.ceil

@Service
class LeadRepositoryImplementation(
    private val customerRepository: CustomerRepository
) : LeadRepository {
    override fun saveLead(lead: Lead): Lead? = transaction {
        val leadUUID = UUID.randomUUID()

        LeadDatabase.insert {
            it[uuid] = leadUUID
            it[document] = lead.document!!
            it[companyName] = lead.companyName!!
            it[tradingName] = lead.tradingName!!
            it[personName] = lead.personName!!
            it[email] = lead.email!!
            it[state] = lead.state!!
            it[city] = lead.city!!
            it[address] = lead.address!!
            it[zipCode] = lead.zipCode!!
            it[complement] = lead.complement!!
            it[addressNumber] = lead.addressNumber!!
            it[status] = lead.status!!.code
            it[createdAt] = lead.createdAt!!
            it[modifiedAt] = lead.modifiedAt!!
        }

        if (!lead.economicActivities.isNullOrEmpty()) {
            lead.economicActivities!!.forEach { economicActivity ->
                LeadEconomicActivitiesDatabase.insert {
                    it[uuid] = UUID.randomUUID()
                    it[economicActivityUUID] = economicActivity.uuid
                    it[LeadEconomicActivitiesDatabase.leadUUID] = leadUUID
                }
            }
        }

        if (!lead.contacts.isNullOrEmpty()) {
            lead.contacts!!.forEach { contact ->
                LeadContactsDatabase.insert {
                    it[uuid] = UUID.randomUUID()
                    it[contactType] = contact.contactType!!.type
                    it[label] = contact.label!!
                    it[isPrincipal] = contact.isPrincipal
                    it[LeadContactsDatabase.leadUUID] = leadUUID
                }
            }
        }

        return@transaction getLeadByUUID(leadUUID)
    }

    override fun approveLead(lead: Lead, userUUID: UUID) {
        transaction {
            val customer = lead.toCustomer(CustomerStatus.FIT)

            customerRepository.registerCustomer(customer, userUUID)

            LeadDatabase.update({ LeadDatabase.uuid eq lead.uuid!! }) {
                it[status] = LeadStatus.APPROVED.code
            }
        }
    }

    override fun rejectLead(leadUUID: UUID) {
        transaction {
            LeadDatabase.update({ LeadDatabase.uuid eq leadUUID }) {
                it[status] = LeadStatus.REPROVED.code
            }
        }
    }

    override fun getLeadByUUID(uuid: UUID): Lead? = transaction {
        LeadDatabase.selectAll().where {
            LeadDatabase.uuid eq uuid
        }.map {
            val lead = resultRowToLead(it)

            lead.contacts = getContactsByLeadUUID(uuid)
            lead.economicActivities = listEconomicActivitiesByLeadUUID(uuid)

            lead
        }.firstOrNull()
    }

    override fun getLeadByDocument(document: String): Lead? = transaction {
        LeadDatabase.selectAll().where {
            LeadDatabase.document eq document and (
                    LeadDatabase.status inList listOf(LeadStatus.APPROVED.code, LeadStatus.PENDING.code)
                    )
        }.map {
            resultRowToLead(it)
        }.firstOrNull()
    }

    override fun getPaginatedLead(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): Pagination<Lead>? =
        transaction {
            val query = LeadDatabase.selectAll()

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    resultRowToLead(it)
                }

            Pagination(
                items = items,
                page = page,
                total = total,
            )
        }

    private fun getContactsByLeadUUID(leadUUID: UUID): List<Contact> = transaction {
        LeadContactsDatabase
            .selectAll()
            .where(LeadContactsDatabase.leadUUID eq leadUUID)
            .map {
                Contact(
                    uuid = it[LeadContactsDatabase.uuid],
                    contactType = enumFromTypeValue<ContactType, String>(it[LeadContactsDatabase.contactType]),
                    label = it[LeadContactsDatabase.label],
                    isPrincipal = it[LeadContactsDatabase.isPrincipal],
                )
            }
    }

    private fun listEconomicActivitiesByLeadUUID(leadUUID: UUID): List<EconomicActivity> = transaction {
        LeadEconomicActivitiesDatabase
            .join(
                otherTable = EconomicActivityDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { EconomicActivityDatabase.uuid eq LeadEconomicActivitiesDatabase.economicActivityUUID }
            )
            .select(
                EconomicActivityDatabase.uuid,
                EconomicActivityDatabase.code,
                EconomicActivityDatabase.description,
                EconomicActivityDatabase.groupCode,
                EconomicActivityDatabase.groupDescription,
                EconomicActivityDatabase.divisionCode,
                EconomicActivityDatabase.divisionDescription,
                EconomicActivityDatabase.sectionCode,
                EconomicActivityDatabase.sectionDescription,
            )
            .where(LeadEconomicActivitiesDatabase.leadUUID eq leadUUID)
            .map {
                EconomicActivity(
                    uuid = it[EconomicActivityDatabase.uuid],
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
            }
    }

    private fun resultRowToLead(it: ResultRow): Lead = Lead(
        uuid = it[LeadDatabase.uuid],
        document = it[LeadDatabase.document],
        companyName = it[LeadDatabase.companyName],
        tradingName = it[LeadDatabase.tradingName],
        personName = it[LeadDatabase.personName],
        email = it[LeadDatabase.email],
        state = it[LeadDatabase.state],
        city = it[LeadDatabase.city],
        zipCode = it[LeadDatabase.zipCode],
        address = it[LeadDatabase.address],
        complement = it[LeadDatabase.complement],
        addressNumber = it[LeadDatabase.addressNumber],
        status = enumFromTypeValue<LeadStatus, Int>(it[LeadDatabase.status]),
        createdAt = it[LeadDatabase.createdAt],
        modifiedAt = it[LeadDatabase.modifiedAt]
    )
}