package com.delice.crm.modules.map.infra.web

import com.delice.crm.modules.map.domain.usecase.MapUseCase
import com.delice.crm.modules.map.domain.usecase.response.MapResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/map")
class MapWebService(
    private val mapUsecase: MapUseCase
) {
    @GetMapping("/customer")
    fun getMapCustomerForState(): ResponseEntity<MapResponse> {
        val response = mapUsecase.getMapCustomerForState()

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