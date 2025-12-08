package com.delice.crm.modules.campaign.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.modules.campaign.domain.entities.CampaignMetadata
import com.delice.crm.modules.kanban.infra.database.mapper
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

object CampaignDatabase : Table("campaign") {
    var uuid = uuid("uuid").uniqueIndex()
    var title = varchar("title", 90)
    var description = text("description")
    var objective = text("objective")
    var status = integer("status")
    var type = integer("type")
    var metadata = json(
        name = "metadata",
        serialize = { mapper.writeValueAsString(it) },
        deserialize = { mapper.readValue<CampaignMetadata>(it) }
    ).nullable()
    var createdBy = uuid("created_by") references UserDatabase.uuid
    var modifiedBy = uuid("modified_by") references UserDatabase.uuid
    var start = datetime("start").nullable()
    var end = datetime("end").nullable()
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(uuid, name = "pk_campaign_uuid")
}