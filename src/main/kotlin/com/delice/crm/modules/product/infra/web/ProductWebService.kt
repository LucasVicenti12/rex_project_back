package com.delice.crm.modules.product.infra.web

import com.delice.crm.core.config.ws.Order
import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.usecase.ProductUseCase
import com.delice.crm.modules.product.domain.usecase.response.ProductMediaResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductPaginationResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/product")
class ProductWebService(
    private val productUseCase: ProductUseCase
) {
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('CREATE_PRODUCT', 'ALL_PRODUCT')")
    fun createProduct(
        @RequestBody product: Product
    ): ResponseEntity<ProductResponse> {
        val response = productUseCase.createProduct(product)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('CREATE_PRODUCT', 'ALL_PRODUCT')")
    fun updateProduct(
        @RequestBody product: Product
    ): ResponseEntity<ProductResponse> {
        val response = productUseCase.updateProduct(product)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getByUUID")
    @PreAuthorize("hasAnyAuthority('READ_PRODUCT', 'ALL_PRODUCT')")
    fun getProductByUUID(
        @RequestParam(
            value = "uuid",
            required = true
        ) uuid: UUID
    ): ResponseEntity<ProductResponse> {
        val response = productUseCase.getProductByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getPagination")
    @PreAuthorize("hasAnyAuthority('READ_PRODUCT', 'ALL_PRODUCT')")
    fun getProductPagination(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        request: HttpServletRequest
    ): ResponseEntity<ProductPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = productUseCase.getProductPagination(page, count, params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/productMedia/save/{productUUID}")
    @PreAuthorize("hasAnyAuthority('CREATE_PRODUCT', 'ALL_PRODUCT')")
    fun saveProductMedia(
        @RequestBody media: List<ProductMedia>,
        @PathVariable(
            value = "productUUID",
            required = true
        ) productUUID: UUID,
    ): ResponseEntity<ProductMediaResponse> {
        val response = productUseCase.saveProductMedia(media, productUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
}