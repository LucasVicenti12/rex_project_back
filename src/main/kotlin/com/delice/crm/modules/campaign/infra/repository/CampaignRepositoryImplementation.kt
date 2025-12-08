package com.delice.crm.modules.campaign.infra.repository

import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.*
import com.delice.crm.modules.campaign.domain.repository.CampaignRepository
import com.delice.crm.modules.campaign.infra.database.CampaignDatabase
import com.delice.crm.modules.product.domain.entities.SerializableProduct
import com.delice.crm.modules.product.domain.repository.ProductRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.ceil

@Service
class CampaignRepositoryImplementation(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
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
        }

        getCampaignByUUID(campaign.uuid!!)
    }

    override fun getCampaignByUUID(campaignUUID: UUID): Campaign? = transaction {
        CampaignDatabase.selectAll().where(CampaignDatabase.uuid eq campaignUUID).map {
            resultRowToCampaign(it)
        }.firstOrNull()
    }

    override fun getAllSaleCampaign(): List<Campaign>? = transaction {
        val currentDate = LocalDateTime.now()

        CampaignDatabase.selectAll().where {
            CampaignDatabase.type eq CampaignType.SALE.code and (
                    (
                            CampaignDatabase.start.isNotNull() and CampaignDatabase.start.lessEq(currentDate)
                            ) or (
                            CampaignDatabase.start.isNull()
                            )
                    ) and (
                    (CampaignDatabase.end.isNotNull() and CampaignDatabase.end.greaterEq(currentDate)) or (
                            CampaignDatabase.end.isNull()
                            )
                    ) and (
                    CampaignDatabase.status eq CampaignStatus.ACTIVE.code
                    )
        }.map {
            resultRowToVisitCampaign(it)
        }
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

    override fun saveCampaignMetadata(
        campaignUUID: UUID,
        metadata: CampaignMetadata?,
        modifiedBy: UUID
    ): Campaign? = transaction {
        CampaignDatabase.update({
            CampaignDatabase.uuid eq campaignUUID
        }) {
            it[CampaignDatabase.modifiedBy] = modifiedBy
            it[modifiedAt] = LocalDateTime.now()
            it[status] = CampaignStatus.ACTIVE.code

            it[CampaignDatabase.metadata] = metadata
        }

        getCampaignByUUID(campaignUUID)
    }

    override fun getVisitCampaign(uuid: UUID): Campaign? = transaction {
        val currentDate = LocalDateTime.now()

        CampaignDatabase.selectAll().where {
            CampaignDatabase.uuid eq uuid and (
                    CampaignDatabase.type eq CampaignType.LEAD.code
                    ) and (
                    (CampaignDatabase.start.isNotNull() and CampaignDatabase.start.lessEq(currentDate)) or (
                            CampaignDatabase.start.isNull()
                            )
                    ) and (
                    (CampaignDatabase.end.isNotNull() and CampaignDatabase.end.greaterEq(currentDate)) or (
                            CampaignDatabase.end.isNull()
                            )
                    ) and (
                    CampaignDatabase.status eq CampaignStatus.ACTIVE.code
                    )
        }.map {
            resultRowToVisitCampaign(it)
        }.firstOrNull()
    }

    private fun resultRowToCampaign(it: ResultRow): Campaign {
        val metadata = it[CampaignDatabase.metadata]

        if (enumFromTypeValue<CampaignType, Int>(it[CampaignDatabase.type]) == CampaignType.SALE) {
            if (metadata?.products != null) {
                metadata.products = metadata.products?.map {
                    val productUUID = UUID.fromString(it.product.uuid)
                    val product = productRepository.getProductByUUID(productUUID)

                    val image = if (product!!.images!!.isNotEmpty()) product.images!![0].image!! else ""

                    DiscountedProduct(
                        product = SerializableProduct(
                            uuid = product.uuid.toString(),
                            code = product.code!!,
                            name = product.name!!,
                            image = image,
                            description = product.description!!,
                            price = product.price!!,
                            weight = product.weight!!
                        ),
                        discount = it.discount
                    )
                }
            }
        }

        return Campaign(
            uuid = it[CampaignDatabase.uuid],
            title = it[CampaignDatabase.title],
            description = it[CampaignDatabase.description],
            objective = it[CampaignDatabase.objective],
            status = enumFromTypeValue<CampaignStatus, Int>(it[CampaignDatabase.status]),
            type = enumFromTypeValue<CampaignType, Int>(it[CampaignDatabase.type]),
            metadata = metadata,
            createdBy = userRepository.getUserByUUID(it[CampaignDatabase.createdBy]),
            modifiedBy = userRepository.getUserByUUID(it[CampaignDatabase.modifiedBy]),
            start = it[CampaignDatabase.start],
            end = it[CampaignDatabase.end],
            createdAt = it[CampaignDatabase.createdAt],
            modifiedAt = it[CampaignDatabase.modifiedAt],
        )
    }

    private fun resultRowToVisitCampaign(it: ResultRow): Campaign = Campaign(
        uuid = it[CampaignDatabase.uuid],
        title = it[CampaignDatabase.title],
        description = it[CampaignDatabase.description],
        status = enumFromTypeValue<CampaignStatus, Int>(it[CampaignDatabase.status]),
        type = enumFromTypeValue<CampaignType, Int>(it[CampaignDatabase.type]),
        metadata = it[CampaignDatabase.metadata],
        start = it[CampaignDatabase.start],
        end = it[CampaignDatabase.end],
        createdAt = it[CampaignDatabase.createdAt],
        modifiedAt = it[CampaignDatabase.modifiedAt],
    )
}

