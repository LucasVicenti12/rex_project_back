package com.delice.crm.modules.menu.infra.webservice

import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.modules.menu.domain.usecase.MenuUseCase
import com.delice.crm.modules.menu.domain.usecase.response.BenchmarkResponse
import com.delice.crm.modules.menu.domain.usecase.response.MenuResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/menu")
class MenuWebService(
    private val menuUseCase: MenuUseCase
) {
    @GetMapping
    fun queryMenuOptions(
        @RequestParam(
            value = "query",
            defaultValue = ""
        ) query: String
    ): ResponseEntity<MenuResponse> {
        val user = getCurrentUser()

        val response = menuUseCase.queryMenuOptions(query, user)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/home")
    fun getHomeResume(): ResponseEntity<BenchmarkResponse> {
        val response = menuUseCase.getHomeResume()

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