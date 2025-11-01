package com.delice.crm.modules.campaign.infra.repository
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignMedia
import com.delice.crm.modules.campaign.domain.entities.CampaignStatus
import com.delice.crm.modules.campaign.domain.repository.CampaignRepository
import com.delice.crm.modules.campaign.infra.database.CampaignDatabase
import com.delice.crm.modules.campaign.infra.database.CampaignMediaDatabase
import com.delice.crm.modules.campaign.infra.database.CampaignProductDatabase
import com.delice.crm.modules.product.domain.entities.SimpleProduct
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.product.domain.repository.ProductRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID
import kotlin.math.ceil


@Service
class CampaignRepositoryImplementation(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : CampaignRepository {
    override fun createCampaign(campaign: Campaign): Campaign? = transaction {
        val campaignUUID = UUID.randomUUID()

        CampaignDatabase.insert {
            it[uuid] = campaignUUID
            it[title] = campaign.title!!
            it[description] = campaign.description!!
            it[status] = campaign.status!!.code
            it[type] = campaign.type!!
            it[channel] = campaign.channel!!
            it[accountable] = campaign.accountable!!
            it[startDate] = campaign.startDate ?: LocalDate.now()
            it[endDate] = campaign.endDate ?: LocalDate.now()
            it[objective] = campaign.objective!!
            it[createdAt] = LocalDate.now()
            it[modifiedAt] = LocalDate.now()
        }

        getCampaignByUUID(campaignUUID)
    }

    override fun updateCampaign(campaign: Campaign): Campaign? = transaction {
        CampaignDatabase.update({
            CampaignDatabase.uuid eq campaign.uuid!!
        }) {
            it[title] = title
            it[description] = campaign.description!!
            it[status] = campaign.status!!.code
            it[type] = campaign.type!!
            it[channel] = campaign.channel!!
            it[startDate] = campaign.startDate ?: LocalDate.now()
            it[endDate] = campaign.endDate ?: LocalDate.now()
            it[objective] = campaign.objective ?: 0.0
            it[accountable] = campaign.accountable!!
            it[modifiedAt] = LocalDate.now()
        }

        getCampaignByUUID(campaign.uuid!!)
    }

    override fun getCampaignByUUID(campaign: UUID): Campaign? = transaction {
        CampaignDatabase.selectAll().where(CampaignDatabase.uuid eq campaign).map {
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
//                .where(CampaignFilter(params).toFilter(CampaignDatabase))
//                .orderBy(CampaignOrderBy(orderBy).toOrderBy())

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

    private fun getCampaignMedia(campaignUUID: UUID): List<CampaignMedia> = transaction {
        CampaignMediaDatabase
            .selectAll()
            .where { CampaignMediaDatabase.campaignUUID eq campaignUUID }
            .map { resultRowToCampaignMedia(it) }
    }

    override fun saveCampaignMedia(media: List<CampaignMedia>, campaignUUID: UUID): List<CampaignMedia> = transaction {
        CampaignMediaDatabase.deleteWhere { CampaignMediaDatabase.campaignUUID eq campaignUUID }
        media.forEach { m ->
            CampaignMediaDatabase.insert {
                it[image] = ExposedBlob(m.image!!.toByteArray())
                it[uuid] = UUID.randomUUID()
                it[isPrincipal] = m.isPrincipal!!
                it[CampaignMediaDatabase.campaignUUID] = campaignUUID
                it[createdAt] = LocalDate.now()
                it[modifiedAt] = LocalDate.now()
            }
        }
        return@transaction getCampaignMedia(campaignUUID)
    }

    override fun getProductCampaign(productUUID: UUID, campaignUUID: UUID?): Campaign? = transaction {
        val query = CampaignDatabase
            .join(
                otherTable = CampaignProductDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { CampaignProductDatabase.productUUID eq productUUID },
            )
            .selectAll()

        if (campaignUUID != null) {
            query.where(CampaignProductDatabase.campaignUUID neq campaignUUID)
        }

        query.map {
            resultRowToCampaign(it)
        }.firstOrNull()
    }

    override fun getFreeProducts(): List<SimpleProduct>? = transaction {
        ProductDatabase
            .join(
                otherTable = CampaignProductDatabase,
                joinType = JoinType.LEFT,
                additionalConstraint = { CampaignProductDatabase.productUUID eq ProductDatabase.uuid }
            )
            .select(
                ProductDatabase.uuid,
                ProductDatabase.name
            ).where {
                CampaignProductDatabase.uuid.isNull()
            }.map {
                SimpleProduct(
                    uuid = it[ProductDatabase.uuid],
                    name = it[ProductDatabase.name]
                )
            }
    }

    private fun resultRowToCampaignMedia(it: ResultRow): CampaignMedia = CampaignMedia(
        uuid = it[CampaignMediaDatabase.uuid],
        campaignUUID = it[CampaignMediaDatabase.campaignUUID],
        image = binaryToString(it[CampaignMediaDatabase.image]),
        isPrincipal = it[CampaignMediaDatabase.isPrincipal],
        viewIndex = it[CampaignMediaDatabase.viewIndex],
        createdAt = it[CampaignMediaDatabase.createdAt],
        modifiedAt = it[CampaignMediaDatabase.modifiedAt],
    )

    private fun resultRowToCampaign(it: ResultRow): Campaign {
        val campaign = Campaign(
            uuid = it[CampaignDatabase.uuid],
            title = it[CampaignDatabase.title],
            description = it[CampaignDatabase.description],
            status = enumFromTypeValue<CampaignStatus, Int>(it[CampaignDatabase.status]),
            type = it[CampaignDatabase.type],
            channel = it[CampaignDatabase.channel],
            startDate = it[CampaignDatabase.startDate],
            endDate = it[CampaignDatabase.endDate],
            objective = it[CampaignDatabase.objective],
            createdAt = it[CampaignDatabase.createdAt],
            modifiedAt = it[CampaignDatabase.modifiedAt],
            accountable = it[CampaignDatabase.accountable]
        )

        campaign.products = CampaignProductDatabase
            .selectAll()
            .where(CampaignProductDatabase.campaignUUID eq campaign.uuid!!)
            .map { product ->
                productRepository.getProductByUUID(
                    product[CampaignProductDatabase.productUUID]
                )!!
            }

        return campaign
    }
}

