package com.delice.crm.api.economicActivities.infra.database

import org.jetbrains.exposed.sql.Table

object EconomicActivityDatabase: Table("economic_activities") {
    val uuid = uuid("uuid").uniqueIndex()
    val code = varchar("code", 5).uniqueIndex()
    val description = varchar("description", 255)
    val groupCode = varchar("group_code", 3)
    val groupDescription = varchar("group_description", 255)
    val divisionCode = varchar("division_code", 2)
    val divisionDescription = varchar("division_description", 255)
    val sectionCode = varchar("section_code", 1)
    val sectionDescription = varchar("section_description", 255)

    override val primaryKey = PrimaryKey(uuid, name = "pk_economic_activities")
}