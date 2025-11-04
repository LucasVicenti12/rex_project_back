package com.delice.crm.modules.dashboard.infra.web

import com.delice.crm.modules.dashboard.domain.usecase.DashboardUsecase
import com.delice.crm.modules.dashboard.domain.usecase.response.DashboardResponse
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
    fun getDashboardCustomer(): ResponseEntity<DashboardResponse> {
        val response = dashboardUsecase.getDashboardCustomer()

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
    fun getDashboardOrder(): ResponseEntity<DashboardResponse> {
        val response = dashboardUsecase.getDashboardOrder()

        if (response?.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/rank")
    fun getDashboardRank(): ResponseEntity<DashboardResponse> {
        val response = dashboardUsecase.getDashboardRank()

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