package com.delice.crm.modules.order.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.order.domain.entities.ManipulateOrder
import com.delice.crm.modules.order.domain.entities.ManipulateOrderItem
import com.delice.crm.modules.order.domain.entities.Order
import com.delice.crm.modules.order.domain.usecase.OrderUseCase
import com.delice.crm.modules.order.domain.usecase.response.OrderPaginationResponse
import com.delice.crm.modules.order.domain.usecase.response.OrderResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/order")
class OrderWebService(
    private val orderUseCase: OrderUseCase,
) {
    @PostMapping("/createOrder")
    fun createOrder(
        @RequestBody order: Order
    ): ResponseEntity<OrderResponse> {
        val response = orderUseCase.createOrder(order)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/saveOrder/{orderUUID}")
    fun saveOrder(
        @PathVariable(
            name = "orderUUID",
            required = true,
        ) orderUUID: UUID,
        @RequestBody manipulateOrder: ManipulateOrder
    ): ResponseEntity<OrderResponse> {
        val response = orderUseCase.saveOrder(orderUUID, manipulateOrder)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/saveOrderItem/{orderUUID}")
    fun saveOrderItem(
        @PathVariable(
            name = "orderUUID",
            required = true,
        ) orderUUID: UUID,
        @RequestBody manipulateOrderItem: ManipulateOrderItem
    ): ResponseEntity<OrderResponse> {
        val response = orderUseCase.saveOrderItem(orderUUID, manipulateOrderItem)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/removeOrderItem/{orderUUID}")
    fun removeOrderItem(
        @PathVariable(
            name = "orderUUID",
            required = true,
        ) orderUUID: UUID,
        @RequestBody manipulateOrderItem: ManipulateOrderItem
    ): ResponseEntity<OrderResponse> {
        val response = orderUseCase.removeOrderItem(orderUUID, manipulateOrderItem)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getOrderByUUID/{orderUUID}")
    fun getOrderByUUID(
        @PathVariable(
            name = "orderUUID",
            required = true,
        ) orderUUID: UUID
    ): ResponseEntity<OrderResponse> {
        val response = orderUseCase.getOrderByUUID(orderUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getPaginatedOrder")
    fun getPaginatedOrder(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        request: HttpServletRequest
    ): ResponseEntity<OrderPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = orderUseCase.getPaginatedOrder(count, page, params)

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