package com.delice.crm.modules.customer.infra.database


import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.api.economicActivities.infra.database.EconomicActivityDatabase
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CustomerDatabase : Table("customer") {
    var uuid = uuid("uuid").uniqueIndex()
    var companyName = varchar("company_name", 90)
    var tradingName = varchar("trading_name", 90)
    var personName = varchar("person_name", 60)
    val document = varchar("document", 14).uniqueIndex()
    var state = char("state", 2)
    var city = varchar("city", 60)
    var address = varchar("address", 150)
    var zipCode = varchar("zip_code", 8)
    var complement = varchar("complement", 60)
    var addressNumber = integer("address_number")
    var observation = text("observation").nullable()
    var status = integer("status")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")
    var createdBy = uuid("created_by") references UserDatabase.uuid
    var modifiedBy = uuid("modified_by") references UserDatabase.uuid

    override val primaryKey = PrimaryKey(uuid, name = "pk_customer")
}

object CustomerEconomicActivitiesDatabase : Table("customer_economic_activities") {
    var uuid = uuid("uuid").uniqueIndex()
    var customerUUID = uuid("customer_uuid") references CustomerDatabase.uuid
    var economicActivityUUID = uuid("economic_activity_uuid") references EconomicActivityDatabase.uuid
}

object CustomerContactsDatabase : Table("customer_contacts") {
    var uuid = uuid("uuid").uniqueIndex()
    var contactType = varchar("contact_type", 10)
    var label = text("label")
    var isPrincipal = bool("is_principal")
    var customerUUID = uuid("customer_uuid") references CustomerDatabase.uuid
}