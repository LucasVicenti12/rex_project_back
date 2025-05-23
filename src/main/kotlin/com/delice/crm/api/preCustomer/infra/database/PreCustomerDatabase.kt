package com.delice.crm.api.preCustomer.infra.database

import org.jetbrains.exposed.sql.Table

object PreCustomerDatabase : Table("pre_customer") {
    val uuid = uuid("uuid").uniqueIndex()
    val companyName = varchar("company_name", 90)
    val tradingName = varchar("trading_name", 90)
    val personName = varchar("person_name", 60)
    val document = varchar("document", 14).uniqueIndex()
    var state = char("state", 2)
    var city = varchar("city", 60)
    var address = varchar("address", 150)
    var zipCode = varchar("zip_code", 8)
    var complement = varchar("complement", 60)
    var addressNumber = integer("address_number")

    override val primaryKey = PrimaryKey(uuid, name = "pk_pre_customer")
}

object PreCustomerEconomicActivitiesDatabase : Table("pre_customer_economic_activities") {
    var uuid = uuid("uuid").uniqueIndex()
    var customerUUID = uuid("customer_uuid") references PreCustomerDatabase.uuid
    var code = varchar("code", 5)
}

object PreCustomerContactsDatabase : Table("pre_customer_contacts") {
    var uuid = uuid("uuid").uniqueIndex()
    var contactType = varchar("contact_type", 10)
    var label = text("label")
    var isPrincipal = bool("is_principal")
    var customerUUID = uuid("customer_uuid") references PreCustomerDatabase.uuid
}