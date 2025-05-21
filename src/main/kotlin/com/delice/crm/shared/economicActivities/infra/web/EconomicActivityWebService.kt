package com.delice.crm.shared.economicActivities.infra.web

import com.delice.crm.shared.economicActivities.domain.usecase.EconomicActivityUseCase
import com.delice.crm.shared.economicActivities.domain.usecase.reponse.EconomicActivityResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/economicActivities")
class EconomicActivityWebService(
    private val economicActivityUseCase: EconomicActivityUseCase
) {
    @GetMapping("/query")
    fun getAddressInViaCepBase(
        @RequestParam(
            value = "code",
            required = true
        ) code: String
    ): ResponseEntity<EconomicActivityResponse> {
        val response = economicActivityUseCase.getEconomicActivity(code)

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