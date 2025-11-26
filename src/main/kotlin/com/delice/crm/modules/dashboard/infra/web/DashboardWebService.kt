package com.delice.crm.modules.dashboard.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.dashboard.domain.usecase.DashboardUsecase
import com.delice.crm.modules.dashboard.domain.usecase.response.DashboardResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dashboard")
class DashboardWebService(
    private val dashboardUsecase: DashboardUsecase
) {
    @GetMapping("/customer")
    fun getDashboardCustomer(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardCustomer(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/order")
    fun getDashboardOrder(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardOrder(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/rank/best/products")
    fun getDashboardRankBest(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardRankBest(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/rank/less/products")
    fun getDashboardRankLess(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardRankLess(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/totalSold")
    fun getDashboardTotalSold(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardTotalSold(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/mostWalletSold")
    fun getDashboardMostWalletSold(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardMostWalletSold(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/mostOperatorSold")
    fun getDashboardMostOperatorSold(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardMostOperatorSold(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/monthSold")
    fun getDashboardMonthSold(request: HttpServletRequest): ResponseEntity<DashboardResponse> {
        val params = request.queryString.parametersToMap()
        val response = dashboardUsecase.getDashboardMonthSold(params)

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
}