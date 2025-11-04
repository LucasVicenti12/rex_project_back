package com.delice.crm.modules.campaign.infra.repository

import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignStatus
import com.delice.crm.modules.campaign.domain.entities.CampaignType
import com.delice.crm.modules.campaign.domain.repository.CampaignRepository
import com.delice.crm.modules.campaign.infra.database.CampaignDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.ceil

@Service
class CampaignRepositoryImplementation(
    private val userRepository: UserRepository
) : CampaignRepository {
    override fun createCampaign(campaign: Campaign): Campaign? = transaction {
        val campaignUUID = UUID.randomUUID()

        CampaignDatabase.insert {
            it[uuid] = campaignUUID
            it[title] = campaign.title!!
            it[description] = campaign.description!!
            it[objective] = campaign.objective!!
            it[status] = campaign.status!!.code
            it[type] = campaign.type!!.code
            it[createdBy] = campaign.createdBy!!.uuid!!
            it[modifiedBy] = campaign.modifiedBy!!.uuid!!
            it[start] = campaign.start
            it[end] = campaign.end
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()

            if (campaign.metadata != null) {
                it[metadata] = campaign.metadata
            }
        }

        getCampaignByUUID(campaignUUID)
    }

    override fun updateCampaign(campaign: Campaign): Campaign? = transaction {
        CampaignDatabase.update({
            CampaignDatabase.uuid eq campaign.uuid!!
        }) {
            it[title] = title
            it[description] = campaign.description!!
            it[objective] = campaign.objective!!
            it[status] = campaign.status!!.code
            it[type] = campaign.type!!.code
            it[start] = campaign.start
            it[end] = campaign.end
            it[modifiedBy] = campaign.modifiedBy!!.uuid!!
            it[modifiedAt] = LocalDateTime.now()

            if (campaign.metadata != null) {
                it[metadata] = campaign.metadata
            }
        }

        getCampaignByUUID(campaign.uuid!!)
    }

    override fun getCampaignByUUID(campaignUUID: UUID): Campaign? = transaction {
        CampaignDatabase.selectAll().where(CampaignDatabase.uuid eq campaignUUID).map {
            resultRowToCampaign(it)
        }.firstOrNull()
    }

    override fun getCampaignPagination(
        page: Int,
        count: Int,
        orderBy: OrderBy?,
        params: Map<String, Any?>
    ): Pagination<Campaign>? =
        transaction {
            val query = CampaignDatabase
                .selectAll()

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    resultRowToCampaign(it)
                }

            Pagination(
                items = items,
                page = page,
                total = total,
            )
        }

    private fun resultRowToCampaign(it: ResultRow): Campaign = Campaign(
        uuid = it[CampaignDatabase.uuid],
        title = it[CampaignDatabase.title],
        description = it[CampaignDatabase.description],
        objective = it[CampaignDatabase.objective],
        status = enumFromTypeValue<CampaignStatus, Int>(it[CampaignDatabase.status]),
        type = enumFromTypeValue<CampaignType, Int>(it[CampaignDatabase.type]),
        metadata = it[CampaignDatabase.metadata],
        createdBy = userRepository.getUserByUUID(it[CampaignDatabase.createdBy]),
        modifiedBy = userRepository.getUserByUUID(it[CampaignDatabase.modifiedBy]),
        start = it[CampaignDatabase.start],
        end = it[CampaignDatabase.end],
        createdAt = it[CampaignDatabase.createdAt],
        modifiedAt = it[CampaignDatabase.modifiedAt],
    )
}

