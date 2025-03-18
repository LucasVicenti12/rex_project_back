package com.delice.crm.core.auth.infra.web

import com.delice.crm.core.auth.domain.entities.Login
import com.delice.crm.core.auth.domain.entities.Register
import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.auth.domain.usecase.response.LoginResponse
import com.delice.crm.core.auth.domain.usecase.response.RegisterResponse
import com.delice.crm.core.config.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthWebService(
    private val authUseCase: AuthUseCase
) {
    @Autowired
    private lateinit var tokenService: TokenService

    @PostMapping("/login")
    fun login(@RequestBody login: Login): ResponseEntity<LoginResponse> {
        val response = authUseCase.loginUser(login)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.SET_COOKIE,
                tokenService.generateTokenCookie(response.token!!).toString()
            )
            .body(response)
    }

    @PostMapping("/register")
    fun register(@RequestBody register: Register): ResponseEntity<RegisterResponse> {
        val response = authUseCase.registerUser(register)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @RequestMapping("/logout", method = [RequestMethod.POST, RequestMethod.GET])
    fun logout(): ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.SET_COOKIE, tokenService.getCleanCookie().toString())
            .header(HttpHeaders.LOCATION, "/web/login")
            .build()
    }
}