package com.delice.crm.core.auth.infra.web

import com.delice.crm.core.auth.domain.entities.Login
import com.delice.crm.core.auth.domain.entities.Register
import com.delice.crm.core.auth.domain.entities.ResetPassword
import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.auth.domain.usecase.response.*
import com.delice.crm.core.config.service.TokenService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

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
            .header(HttpHeaders.LOCATION, "/app/login")
            .build()
    }

    @GetMapping("/authenticated")
    fun getAuthenticated(request: HttpServletRequest): ResponseEntity<AuthenticatedResponse> {
        val token = tokenService.recoverToken(request)
        val login = tokenService.validate(token)
        val user = authUseCase.findUserByLogin(login)

        if (user === null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        val response = authUseCase.getAuthenticated(user.uuid!!)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/forgotPassword")
    fun forgotPassword(
        @RequestParam(
            value = "email",
            required = true
        ) email: String,
        request: HttpServletRequest
    ): ResponseEntity<ForgotPasswordResponse> {
        val host = "${request.scheme}://${request.getHeader("Host")}"

        val response = authUseCase.forgotPassword(email, host)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/changePassword")
    fun resetPassword(
        @RequestBody resetPassword: ResetPassword,
        request: HttpServletRequest
    ): ResponseEntity<ChangePasswordResponse> {
        val token = tokenService.recoverToken(request)
        val login = tokenService.validate(token)
        val user = authUseCase.findUserByLogin(login) ?: return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(null)

        val response = authUseCase.resetPassword(user.uuid!!, resetPassword)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/resetPassword")
    fun resetPasswordWithToken(
        @RequestParam(
            value = "token",
            required = true
        ) token: String,
        @RequestBody resetPassword: ResetPassword
    ): ResponseEntity<ChangePasswordResponse> {
        val response = authUseCase.resetPasswordWithToken(resetPassword, token)

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