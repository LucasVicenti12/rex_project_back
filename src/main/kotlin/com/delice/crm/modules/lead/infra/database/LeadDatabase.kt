package com.delice.crm.modules.lead.infra.database

import com.delice.crm.api.economicActivities.infra.database.EconomicActivityDatabase
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object LeadDatabase : Table("lead") {
    var uuid = uuid("uuid").uniqueIndex()
    val document = varchar("document", 14)
    var companyName = varchar("company_name", 90)
    var tradingName = varchar("trading_name", 90)
    var personName = varchar("person_name", 60)
    var email = varchar("email", 90)
    var state = char("state", 2)
    var city = varchar("city", 60)
    var address = varchar("address", 150)
    var zipCode = varchar("zip_code", 8)
    var complement = varchar("complement", 60)
    var addressNumber = integer("address_number")
    var status = integer("status")
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_lead")
}

object LeadEconomicActivitiesDatabase : Table("lead_economic_activities") {
    var uuid = uuid("uuid").uniqueIndex()
    var leadUUID = uuid("lead_uuid") references LeadDatabase.uuid
    var economicActivityUUID = uuid("economic_activity_uuid") references EconomicActivityDatabase.uuid
}

object LeadContactsDatabase : Table("lead_contacts") {
    var uuid = uuid("uuid").uniqueIndex()
    var contactType = varchar("contact_type", 10)
    var label = text("label")
    var isPrincipal = bool("is_principal")
    var leadUUID = uuid("lead_uuid") references LeadDatabase.uuid
}