package com.delice.crm.modules.campaign.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.campaign.domain.entities.Campaign
import com.delice.crm.modules.campaign.domain.entities.CampaignMetadata
import com.delice.crm.modules.campaign.domain.usecase.CampaignUseCase
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignPaginationResponse
import com.delice.crm.modules.campaign.domain.usecase.response.CampaignResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/campaign")
class CampaignWebService(
    private val campaignUseCase: CampaignUseCase
) {
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('CREATE_CAMPAIGN', 'ALL_CAMPAIGN')")
    fun createCampaign(
        @RequestBody campaign: Campaign
    ): ResponseEntity<CampaignResponse> {
        val response = campaignUseCase.createCampaign(campaign)

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
    @PreAuthorize("hasAnyAuthority('CREATE_CAMPAIGN', 'ALL_CAMPAIGN')")
    fun updateCampaign(
        @RequestBody campaign: Campaign
    ): ResponseEntity<CampaignResponse> {
        val response = campaignUseCase.updateCampaign(campaign)

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
    @PreAuthorize("hasAnyAuthority('CREATE_CAMPAIGN', 'READ_CAMPAIGN', 'ALL_CAMPAIGN')")
    fun getCampaignPagination(
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
    ): ResponseEntity<CampaignPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = campaignUseCase.getCampaignPagination(page, count, orderBy, params)

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
    fun getCampaignByUUID(
        @PathVariable(
            value = "uuid",
            required = true
        ) campaignUUID: UUID
    ): ResponseEntity<CampaignResponse> {
        val response = campaignUseCase.getCampaignByUUID(campaignUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/metadata/{uuid}")
    fun saveCampaignMetadata(
        @PathVariable(
            value = "uuid",
            required = true
        ) campaignUUID: UUID,
        @RequestBody metadata: CampaignMetadata?,
    ): ResponseEntity<CampaignResponse> {
        val response = campaignUseCase.saveCampaignMetadata(campaignUUID, metadata)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/visit/{uuid}")
    fun saveCampaignMetadata(
        @PathVariable(
            value = "uuid",
            required = true
        ) campaignUUID: UUID
    ): ResponseEntity<CampaignResponse> {
        val response = campaignUseCase.getVisitCampaign(campaignUUID)

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