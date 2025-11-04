package com.delice.crm.modules.lead.infra.webservice

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.lead.domain.entities.Lead
import com.delice.crm.modules.lead.domain.usecase.LeadUseCase
import com.delice.crm.modules.lead.domain.usecase.response.LeadApprovalResponse
import com.delice.crm.modules.lead.domain.usecase.response.LeadPaginationResponse
import com.delice.crm.modules.lead.domain.usecase.response.LeadResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/lead")
class LeadWebService(
    private val leadUseCase: LeadUseCase
) {
    @PostMapping("/save")
    fun saveLead(@RequestBody lead: Lead): ResponseEntity<LeadResponse> {
        val response = leadUseCase.saveLead(lead)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PutMapping("/approve/{uuid}")
    fun approveLead(
        @PathVariable(
            value = "uuid",
            required = true
        ) uuid: UUID
    ): ResponseEntity<LeadApprovalResponse> {
        val response = leadUseCase.approveLead(uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PutMapping("/reprove/{uuid}")
    fun rejectLead(
        @PathVariable(
            value = "uuid",
            required = true
        ) uuid: UUID
    ): ResponseEntity<LeadApprovalResponse> {
        val response = leadUseCase.rejectLead(uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/{uuid}")
    fun getLeadByUUID(
        @PathVariable(
            value = "uuid",
            required = true
        ) uuid: UUID
    ): ResponseEntity<LeadResponse> {
        val response = leadUseCase.getLeadByUUID(uuid)

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
    fun getPaginatedLead(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        @RequestParam(
            value = "orderBy",
            required = false
        ) orderBy: OrderBy,
        request: HttpServletRequest
    ): ResponseEntity<LeadPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = leadUseCase.getPaginatedLead(page, count, orderBy, params)

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