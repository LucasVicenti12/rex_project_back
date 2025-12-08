package com.delice.crm.modules.lead.infra.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object LeadDatabase : Table("lead") {
    var uuid = uuid("uuid").uniqueIndex()
    val document = varchar("document", 14)
    var companyName = varchar("company_name", 90)
    var tradingName = varchar("trading_name", 90)
    var personName = varchar("person_name", 60)
    var email = varchar("email", 90)
    var phone = varchar("phone", 20)
    var economicActivity = varchar("economic_activity", 10)
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