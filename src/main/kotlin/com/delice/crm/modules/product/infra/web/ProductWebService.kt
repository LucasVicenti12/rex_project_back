package com.delice.crm.modules.product.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.usecase.ProductUseCase
import com.delice.crm.modules.product.domain.usecase.response.ProductPaginationResponse
import com.delice.crm.modules.product.domain.usecase.response.ProductResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/product")
class ProductWebService(
    private val productUseCase: ProductUseCase
) {
    @PostMapping("/create")
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
}