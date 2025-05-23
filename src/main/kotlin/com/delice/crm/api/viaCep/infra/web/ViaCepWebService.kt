package com.delice.crm.api.viaCep.infra.web

import com.delice.crm.api.viaCep.domain.usecase.ViaCepUseCase
import com.delice.crm.api.viaCep.domain.usecase.response.ViaCepAddressResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/address")
class ViaCepWebService(
    private val viaCepUseCase: ViaCepUseCase
) {
    @GetMapping("/query")
    fun getAddressInViaCepBase(
        @RequestParam(
            value = "zipCode",
            required = true
        ) zipCode: String
    ): ResponseEntity<ViaCepAddressResponse> {
        val response = viaCepUseCase.getAddressInViaCepBase(zipCode)

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