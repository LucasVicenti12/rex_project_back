package com.delice.crm.modules.campaign.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.modules.product.infra.database.ProductDatabase
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object CampaignDatabase : Table("campaign") {
    var uuid = uuid("uuid").uniqueIndex()
    var title = varchar("title", 90)
    var description = varchar("description", 1000)
    var status = integer("status")
    var type = varchar("type", 90)
    var channel =  varchar("channel", 90)
    var objective = double("objective")
    var startDate = date("startDate")
    var endDate = date("endDate")
    var createdAt = date("created_at")
    var modifiedAt = date("modified_at")
    var accountable = uuid("accountable") references UserDatabase.uuid

    override val primaryKey = PrimaryKey(uuid, name = "pk_campaign_uuid")
}

object CampaignMediaDatabase : Table("campaign_media") {
    val uuid = uuid("uuid").uniqueIndex()
    val campaignUUID = uuid("campaignUUID") references CampaignDatabase.uuid
    val image = blob("image")
    val isPrincipal = bool("is_principal")
    var viewIndex = integer("viewIndex")
    val createdAt = date("created_at")
    val modifiedAt = date("modified_at")
}

object CampaignProductDatabase : Table("campaign_product") {
    val uuid = uuid("uuid").uniqueIndex()
    var campaignUUID = uuid("campaign_uuid") references CampaignDatabase.uuid
    var productUUID = uuid("product_uuid") references ProductDatabase.uuid
}
