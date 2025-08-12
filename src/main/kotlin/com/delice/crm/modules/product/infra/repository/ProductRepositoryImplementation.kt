package com.delice.crm.modules.product.infra.repository

import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.entities.ProductStatus
import com.delice.crm.modules.product.domain.repository.ProductRepository
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.product.infra.database.ProductDatabase.code
import com.delice.crm.modules.product.infra.database.ProductDatabase.createdAt
import com.delice.crm.modules.product.infra.database.ProductDatabase.description
import com.delice.crm.modules.product.infra.database.ProductDatabase.modifiedAt
import com.delice.crm.modules.product.infra.database.ProductDatabase.name
import com.delice.crm.modules.product.infra.database.ProductDatabase.price
import com.delice.crm.modules.product.infra.database.ProductDatabase.status
import com.delice.crm.modules.product.infra.database.ProductDatabase.uuid
import com.delice.crm.modules.product.infra.database.ProductDatabase.weight
import com.delice.crm.modules.product.infra.database.ProductFilter
import com.delice.crm.modules.product.infra.database.ProductMediaDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import kotlin.math.ceil

@Service
class ProductRepositoryImplementation : ProductRepository {
    override fun createProduct(product: Product): Product? = transaction {
        val productUUID = UUID.randomUUID()

        ProductDatabase.insert {
            it[uuid] = productUUID
            it[code] = product.code!!
            it[name] = product.name!!
            it[description] = product.description!!
            it[price] = product.price!!
            it[weight] = product.weight!!
            it[status] = product.status!!.code
            it[createdAt] = LocalDate.now()
            it[modifiedAt] = LocalDate.now()
        }

        getProductByUUID(productUUID)
    }

    override fun updateProduct(product: Product): Product? = transaction {
        ProductDatabase.update({ uuid eq product.uuid!! }) {
            it[name] = product.name!!
            it[description] = product.description!!
            it[price] = product.price!!
            it[weight] = product.weight!!
            it[status] = product.status!!.code
            it[modifiedAt] = LocalDate.now()
        }

        getProductByUUID(product.uuid!!)
    }

    override fun saveProductMedia(media: List<ProductMedia>, productUUID: UUID): List<ProductMedia> = transaction {
        ProductMediaDatabase.deleteWhere { ProductMediaDatabase.productUUID eq productUUID }
        media.forEach { m ->
            ProductMediaDatabase.insert {
                it[image] = ExposedBlob(m.image!!.toByteArray())
                it[uuid] = UUID.randomUUID()
                it[isPrincipal] = m.isPrincipal!!
                it[ProductMediaDatabase.productUUID] = productUUID
                it[createdAt] = LocalDate.now()
                it[modifiedAt] = LocalDate.now()
            }
        }
        return@transaction getProductMedia(productUUID)
    }

    override fun getProductByUUID(uuid: UUID): Product? = transaction {
        ProductDatabase.selectAll().where {
            ProductDatabase.uuid eq uuid
        }.map {
            resultRowToProduct(it)
        }.firstOrNull()
    }

    override fun getProductByCode(code: String): Product? = transaction {
        ProductDatabase.selectAll().where {
            ProductDatabase.code eq code
        }.map {
            resultRowToProduct(it)
        }.firstOrNull()
    }

    override fun getProductPagination(page: Int, count: Int, orderBy: String?, params: Map<String, Any?>): Pagination<Product>? =
        transaction {
            val query = ProductDatabase
                .selectAll()
                .where(ProductFilter(params).toFilter(ProductDatabase))
                .orderBy(*parseOrderBy(orderBy).toTypedArray())

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    resultRowToProduct(it)
                }

            Pagination(
                items = items,
                page = page,
                total = total,
            )
        }

    private fun parseOrderBy(orderBy: String?): List<Pair<Expression<*>, SortOrder>> {
        if (orderBy.isNullOrBlank()) return listOf(ProductDatabase.name to SortOrder.ASC)

        // Maps valid field names to database columns
        val fieldMap = mapOf(
            "uuid" to ProductDatabase.uuid,
            "code" to ProductDatabase.code,
            "name" to ProductDatabase.name,
            "price" to ProductDatabase.price,
            "weight" to ProductDatabase.weight,
            "createdAt" to ProductDatabase.createdAt,
            "modifiedAt" to ProductDatabase.modifiedAt,
            "status" to ProductDatabase.status
        )

        return orderBy.split(",").map { raw ->
            val parts = raw.trim().split(":")
            val fieldName = parts.getOrNull(0)?.trim() ?: throw IllegalArgumentException(
                "Empty sort field detected in '$raw'. check the 'orderBy' parameter."
            )

            val sortOrder = when (parts.getOrNull(1)?.trim()?.lowercase()) {
                "desc" -> SortOrder.DESC
                null, "", "asc" -> SortOrder.ASC
                else -> throw IllegalArgumentException(
                    "Invalid sort direction for field '$fieldName'. Use only 'asc' or 'desc'."
                )
            }

            val column = fieldMap[fieldName] ?: throw IllegalArgumentException(
                "Field '$fieldName' is not valid for sorting. Allowed fields: ${fieldMap.keys.joinToString()}."
            )

            column to sortOrder
        }
    }


    private fun getProductMedia(productUUID: UUID): List<ProductMedia> = transaction {
        ProductMediaDatabase
            .selectAll()
            .where { ProductMediaDatabase.productUUID eq productUUID }
            .map { resultRowToProductMedia(it) }
    }


    private fun resultRowToProductMedia(it: ResultRow): ProductMedia = ProductMedia(
        uuid = it[ProductMediaDatabase.uuid],
        image = binaryToString(it[ProductMediaDatabase.image]),
        isPrincipal = it[ProductMediaDatabase.isPrincipal],
        createdAt = it[ProductMediaDatabase.createdAt],
        modifiedAt = it[ProductMediaDatabase.modifiedAt],
    )

    private fun resultRowToProduct(it: ResultRow): Product = Product(
        uuid = it[uuid],
        code = it[code],
        name = it[name],
        description = it[description],
        price = it[price],
        weight = it[weight],
        status = enumFromTypeValue<ProductStatus, Int>(it[status]),
        createdAt = it[createdAt],
        modifiedAt = it[modifiedAt],
        images = getProductMedia(it[uuid])
    )
}