package com.delice.crm.shared.viaCep.infra.database

import org.jetbrains.exposed.sql.Table

object AddressDatabase : Table("address") {
    val uuid = uuid("uuid").uniqueIndex()
    val zipCode = varchar("zip_code", 8).uniqueIndex()
    val address = varchar("address", 90)
    val district = varchar("district", 90)
    val city = varchar("city", 90)
    val state = char("state", 2)
}