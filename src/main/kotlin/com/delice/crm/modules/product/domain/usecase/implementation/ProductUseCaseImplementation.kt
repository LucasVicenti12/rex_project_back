package com.delice.crm.modules.product.domain.usecase.implementation

import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.exceptions.*
import com.delice.crm.modules.product.domain.repository.ProductRepository
import com.delice.crm.modules.product.domain.usecase.ProductUseCase
import com.delice.crm.modules.product.domain.usecase.response.ProductMediaResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductPaginationResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductUseCaseImplementation(
    private val productRepository: ProductRepository
) : ProductUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(ProductUseCaseImplementation::class.java)
    }

    override fun createProduct(product: Product): ProductResponse = try {
        val validate = validateProduct(product)

        when {
            validate.error != null -> {
                validate
            }

            productRepository.getProductByCode(product.code!!) != null -> {
                ProductResponse(error = PRODUCT_ALREADY_EXISTS)
            }

            else -> {
                ProductResponse(product = productRepository.createProduct(product))
            }
        }
    } catch (e: Exception) {
        logger.error("CREATE_PRODUCT", e)
        ProductResponse(error = PRODUCT_UNEXPECTED_ERROR)
    }

    override fun updateProduct(product: Product): ProductResponse = try {
        val validate = validateProduct(product)

        when {
            validate.error != null -> {
                validate
            }

            product.uuid == null -> {
                ProductResponse(error = PRODUCT_NOT_FOUND)
            }

            productRepository.getProductByUUID(product.uuid) == null -> {
                ProductResponse(error = PRODUCT_NOT_FOUND)
            }

            else -> {
                ProductResponse(product = productRepository.updateProduct(product))
            }
        }
    } catch (e: Exception) {
        logger.error("UPDATE_PRODUCT", e)
        ProductResponse(error = PRODUCT_UNEXPECTED_ERROR)
    }

    override fun saveProductMedia(media: List<ProductMedia>, productUUID: UUID): ProductMediaResponse {
        try {
            productRepository.getProductByUUID(productUUID)?: return ProductMediaResponse(error = PRODUCT_NOT_FOUND)

            val hasMorePrincipal = media.count { it.isPrincipal == true }

            if(hasMorePrincipal == 0) return ProductMediaResponse(error = PRODUCT_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL)

            if(hasMorePrincipal > 1) return ProductMediaResponse(error = PRODUCT_MEDIA_MUST_BE_PRINCIPAL)

            productRepository.saveProductMedia(media, productUUID)
            return ProductMediaResponse(media = media)
        } catch (e: Exception) {
            logger.error("SAVE_PRODUCT_MEDIA", e)
            return ProductMediaResponse(error = PRODUCT_UNEXPECTED_ERROR)
        }
    }

    override fun getProductByUUID(uuid: UUID): ProductResponse = try {
        val product = productRepository.getProductByUUID(uuid)

        if (product == null) {
            ProductResponse(error = PRODUCT_NOT_FOUND)
        } else {
            ProductResponse(product = product)
        }
    } catch (e: Exception) {
        logger.error("GET_PRODUCT_BY_UUID", e)
        ProductResponse(error = PRODUCT_UNEXPECTED_ERROR)
    }

    override fun getProductPagination(page: Int, count: Int, orderBy: String?, params: Map<String, Any?>): ProductPaginationResponse {
        return try {
            return ProductPaginationResponse(
                products = productRepository.getProductPagination(page, count, orderBy, params),
                error = null
            )
        } catch (e: Exception) {
            logger.error("GET_PRODUCT_PAGINATION", e)
            ProductPaginationResponse(error = PRODUCT_UNEXPECTED_ERROR)
        }
    }

    private fun validateProduct(product: Product): ProductResponse = when {
        product.code.isNullOrBlank() -> {
            ProductResponse(error = PRODUCT_CODE_IS_EMPTY)
        }

        product.name.isNullOrBlank() -> {
            ProductResponse(error = PRODUCT_NAME_IS_EMPTY)
        }

        product.price == 0.0 -> {
            ProductResponse(error = PRODUCT_PRICE_IS_EMPTY)
        }

        product.weight == 0.0 -> {
            ProductResponse(error = PRODUCT_WEIGHT_IS_EMPTY)
        }

        else -> {
            ProductResponse()
        }
    }
}