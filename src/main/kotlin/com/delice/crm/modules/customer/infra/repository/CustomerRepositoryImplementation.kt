package com.delice.crm.modules.customer.infra.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.api.economicActivities.domain.entities.EconomicActivityAttribute
import com.delice.crm.api.economicActivities.infra.database.EconomicActivityDatabase
import com.delice.crm.core.utils.contact.Contact
import com.delice.crm.core.utils.contact.ContactType
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.modules.customer.domain.entities.SimpleCustomer
import com.delice.crm.modules.customer.infra.database.CustomerContactsDatabase
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.customer.infra.database.CustomerEconomicActivitiesDatabase
import com.delice.crm.modules.customer.infra.database.CustomerFilter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Service
class CustomerRepositoryImplementation() : CustomerRepository {
    override fun registerCustomer(customer: Customer, userUUID: UUID): Customer? = transaction {
        val customerUUID = UUID.randomUUID()

        var tempComplement = ""

        if (customer.complement!!.length > 60) {
            tempComplement = customer.complement.substring(0, 60)
        }

        CustomerDatabase.insert {
            it[document] = customer.document!!
            it[tradingName] = customer.tradingName!!
            it[companyName] = customer.companyName!!
            it[personName] = customer.personName!!
            it[state] = customer.state!!
            it[city] = customer.city!!
            it[address] = customer.address!!
            it[zipCode] = customer.zipCode!!
            it[complement] = tempComplement
            it[addressNumber] = customer.addressNumber!!
            it[observation] = customer.observation!!
            it[status] = customer.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
            it[createdBy] = userUUID
            it[modifiedBy] = userUUID
            it[uuid] = customerUUID
        }

        if (!customer.economicActivities.isNullOrEmpty()) {
            customer.economicActivities!!.forEach { economicActivity ->
                CustomerEconomicActivitiesDatabase.insert {
                    it[uuid] = UUID.randomUUID()
                    it[economicActivityUUID] = economicActivity.uuid
                    it[CustomerEconomicActivitiesDatabase.customerUUID] = customerUUID
                }
            }
        }

        if (!customer.contacts.isNullOrEmpty()) {
            customer.contacts!!.forEach { contact ->
                CustomerContactsDatabase.insert {
                    it[uuid] = UUID.randomUUID()
                    it[contactType] = contact.contactType!!.type
                    it[label] = contact.label!!
                    it[isPrincipal] = contact.isPrincipal
                    it[CustomerContactsDatabase.customerUUID] = customerUUID
                }
            }
        }

        return@transaction getCustomerByUUID(customerUUID)
    }

    override fun updateCustomer(customer: Customer, userUUID: UUID): Customer? = transaction {
        var tempComplement = ""

        tempComplement = if (customer.complement!!.length > 60) {
            customer.complement.substring(0, 60)
        } else {
            customer.complement
        }

        CustomerDatabase.update({ CustomerDatabase.uuid eq customer.uuid!! }) {
            it[tradingName] = customer.tradingName!!
            it[companyName] = customer.companyName!!
            it[personName] = customer.personName!!
            it[state] = customer.state!!
            it[city] = customer.city!!
            it[address] = customer.address!!
            it[zipCode] = customer.zipCode!!
            it[complement] = tempComplement
            it[addressNumber] = customer.addressNumber!!
            it[observation] = customer.observation!!
            it[status] = customer.status!!.code
            it[modifiedAt] = LocalDateTime.now()
            it[modifiedBy] = userUUID
        }

        CustomerEconomicActivitiesDatabase.deleteWhere { customerUUID eq customer.uuid!! }

        if (!customer.economicActivities.isNullOrEmpty()) {
            customer.economicActivities!!.forEach { economicActivity ->
                CustomerEconomicActivitiesDatabase.insert {
                    it[uuid] = UUID.randomUUID()
                    it[economicActivityUUID] = economicActivity.uuid
                    it[customerUUID] = customer.uuid!!
                }
            }
        }

        CustomerContactsDatabase.deleteWhere { customerUUID eq customer.uuid!! }

        if (!customer.contacts.isNullOrEmpty()) {
            customer.contacts!!.forEach { contact ->
                CustomerContactsDatabase.insert {
                    it[uuid] = UUID.randomUUID()
                    it[contactType] = contact.contactType!!.type
                    it[label] = contact.label!!
                    it[isPrincipal] = contact.isPrincipal
                    it[customerUUID] = customer.uuid!!
                }
            }
        }

        return@transaction getCustomerByUUID(customer.uuid!!)
    }

    override fun approvalCustomer(status: CustomerStatus, customerUUID: UUID, userUUID: UUID) {
        transaction {
            CustomerDatabase.update({ CustomerDatabase.uuid eq customerUUID }) {
                it[CustomerDatabase.status] = status.code
            }
        }
    }

    override fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): List<EconomicActivity>? = transaction {
        CustomerEconomicActivitiesDatabase
            .join(
                otherTable = EconomicActivityDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { EconomicActivityDatabase.uuid eq CustomerEconomicActivitiesDatabase.economicActivityUUID }
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
            .where(CustomerEconomicActivitiesDatabase.customerUUID eq customerUUID)
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

    override fun getCustomerByUUID(customerUUID: UUID): Customer? = transaction {
        CustomerDatabase.selectAll()
            .where(CustomerDatabase.uuid eq customerUUID)
            .map {
                val customer = resultRowToCustomer(it)

                customer.contacts = getContactsByCustomerUUID(customerUUID)
                customer.economicActivities = listEconomicActivitiesByCustomerUUID(customerUUID)

                customer
            }.firstOrNull()
    }

    override fun getCustomerByDocument(document: String): Customer? = transaction {
        CustomerDatabase.selectAll()
            .where(CustomerDatabase.document eq document)
            .map {
                resultRowToCustomer(it)
            }.firstOrNull()
    }

    override fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<Customer>? =
        transaction {
            val query = CustomerDatabase
                .selectAll()
                .where(CustomerFilter(params).toFilter(CustomerDatabase))

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .orderBy(CustomerDatabase.companyName)
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    resultRowToCustomer(it)
                }

            Pagination(
                items = items,
                page = page,
                total = total,
            )
        }

    override fun listSimpleCustomer(): List<SimpleCustomer>? = transaction {
        CustomerDatabase.select(CustomerDatabase.uuid, CustomerDatabase.companyName, CustomerDatabase.document)
            .where(CustomerDatabase.status eq CustomerStatus.FIT.code)
            .map {
                SimpleCustomer(
                    uuid = it[CustomerDatabase.uuid],
                    companyName = it[CustomerDatabase.companyName],
                    document = it[CustomerDatabase.document],
                )
            }
    }

    private fun getContactsByCustomerUUID(customerUUID: UUID): List<Contact> = transaction {
        CustomerContactsDatabase
            .selectAll()
            .where(CustomerContactsDatabase.customerUUID eq customerUUID)
            .map {
                Contact(
                    uuid = it[CustomerContactsDatabase.uuid],
                    contactType = enumFromTypeValue<ContactType, String>(it[CustomerContactsDatabase.contactType]),
                    label = it[CustomerContactsDatabase.label],
                    isPrincipal = it[CustomerContactsDatabase.isPrincipal],
                )
            }
    }

    private fun resultRowToCustomer(it: ResultRow): Customer = Customer(
        uuid = it[CustomerDatabase.uuid],
        companyName = it[CustomerDatabase.companyName],
        tradingName = it[CustomerDatabase.tradingName],
        personName = it[CustomerDatabase.personName],
        document = it[CustomerDatabase.document],
        state = it[CustomerDatabase.state],
        city = it[CustomerDatabase.city],
        zipCode = it[CustomerDatabase.zipCode],
        address = it[CustomerDatabase.address],
        complement = it[CustomerDatabase.complement],
        addressNumber = it[CustomerDatabase.addressNumber],
        observation = it[CustomerDatabase.observation],
        status = enumFromTypeValue<CustomerStatus, Int>(it[CustomerDatabase.status]),
        createdAt = it[CustomerDatabase.createdAt],
        modifiedAt = it[CustomerDatabase.modifiedAt],
        createdBy = it[CustomerDatabase.createdBy],
        modifiedBy = it[CustomerDatabase.modifiedBy],
    )
}