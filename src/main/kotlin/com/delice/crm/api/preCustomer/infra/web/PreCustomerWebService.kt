package com.delice.crm.api.preCustomer.infra.web

import com.delice.crm.api.preCustomer.domain.usecase.PreCustomerUseCase
import com.delice.crm.api.preCustomer.domain.usecase.response.PreCustomerResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/preCustomer")
class PreCustomerWebService(
    private val preCustomerUseCase: PreCustomerUseCase
) {
    @GetMapping("/query")
    fun getPreCustomer(
        @RequestParam(
            value = "document",
            required = true
        ) document: String
    ): ResponseEntity<PreCustomerResponse> {
        val response = preCustomerUseCase.getPreCustomer(document)

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