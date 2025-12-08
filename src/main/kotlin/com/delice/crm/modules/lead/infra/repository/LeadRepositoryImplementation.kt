package com.delice.crm.modules.lead.infra.repository

import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.lead.domain.entities.Lead
import com.delice.crm.modules.lead.domain.entities.LeadStatus
import com.delice.crm.modules.lead.domain.repository.LeadRepository
import com.delice.crm.modules.lead.infra.database.LeadDatabase
import org.jetbrains.exposed.sql.*
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
            it[phone] = lead.phone!!
            it[state] = lead.state!!
            it[city] = lead.city!!
            it[address] = lead.address!!
            it[zipCode] = lead.zipCode!!
            it[complement] = lead.complement!!
            it[addressNumber] = lead.addressNumber!!
            it[economicActivity] = lead.economicActivity!!
            it[status] = lead.status!!.code
            it[createdAt] = lead.createdAt!!
            it[modifiedAt] = lead.modifiedAt!!
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
            resultRowToLead(it)
        }.firstOrNull()
    }

    override fun getLeadByDocument(document: String): Lead? = transaction {
        LeadDatabase.selectAll().where {
            LeadDatabase.document eq document
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

    private fun resultRowToLead(it: ResultRow): Lead = Lead(
        uuid = it[LeadDatabase.uuid],
        document = it[LeadDatabase.document],
        companyName = it[LeadDatabase.companyName],
        tradingName = it[LeadDatabase.tradingName],
        personName = it[LeadDatabase.personName],
        email = it[LeadDatabase.email],
        phone = it[LeadDatabase.phone],
        economicActivity = it[LeadDatabase.economicActivity],
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