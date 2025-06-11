package com.delice.crm.modules.product.infra.repository

import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductStatus
import com.delice.crm.modules.product.domain.repository.ProductRepository
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.product.infra.database.ProductDatabase.code
import com.delice.crm.modules.product.infra.database.ProductDatabase.createdAt
import com.delice.crm.modules.product.infra.database.ProductDatabase.description
import com.delice.crm.modules.product.infra.database.ProductDatabase.image
import com.delice.crm.modules.product.infra.database.ProductDatabase.modifiedAt
import com.delice.crm.modules.product.infra.database.ProductDatabase.name
import com.delice.crm.modules.product.infra.database.ProductDatabase.price
import com.delice.crm.modules.product.infra.database.ProductDatabase.status
import com.delice.crm.modules.product.infra.database.ProductDatabase.uuid
import com.delice.crm.modules.product.infra.database.ProductDatabase.weight
import com.delice.crm.modules.product.infra.database.ProductFilter
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
            if (product.image != null) {
                it[image] = ExposedBlob(product.image.toByteArray())
            }
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
            if (product.image != null) {
                it[image] = ExposedBlob(product.image.toByteArray())
            }
            it[price] = product.price!!
            it[weight] = product.weight!!
            it[status] = product.status!!.code
            it[modifiedAt] = LocalDate.now()
        }

        getProductByUUID(product.uuid!!)
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

    override fun getProductPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<Product>? =
        transaction {
            val query = ProductDatabase
                .selectAll()
                .where(ProductFilter(params).toFilter(ProductDatabase))

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .orderBy(name)
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

    private fun resultRowToProduct(it: ResultRow): Product = Product(
        uuid = it[uuid],
        code = it[code],
        name = it[name],
        description = it[description],
        image = binaryToString(it[image]),
        price = it[price],
        weight = it[weight],
        status = enumFromTypeValue<ProductStatus, Int>(it[status]),
        createdAt = it[createdAt],
        modifiedAt = it[modifiedAt],
    )
}