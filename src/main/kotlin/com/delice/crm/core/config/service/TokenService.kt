package com.delice.crm.core.config.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.delice.crm.core.config.entities.TokenType
import com.delice.crm.core.user.domain.entities.User
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class TokenService {
    @Value("\${system.security.jwt.secretKey}")
    lateinit var secretKey: String

    @Value("\${system.security.jwt.cookieName}")
    lateinit var cookieName: String

    @Value("\${system.security.jwt.issuer}")
    lateinit var issuer: String

    fun generate(user: User, tokenType: TokenType): String {
        return try {
            val expires = when (tokenType) {
                TokenType.AUTH_REQUEST -> generateAuthRequestExpiration()
                TokenType.RESET_REQUEST -> generateResetRequestExpiration()
            }

            JWT.create()
                .withIssuer(issuer)
                .withSubject(user.login)
                .withExpiresAt(expires)
                .sign(
                    Algorithm.HMAC256(secretKey)
                )
        } catch (e: JWTCreationException) {
            throw RuntimeException("Error on create token", e)
        }
    }

    fun validate(token: String): String {
        return try {
            JWT.require(
                Algorithm.HMAC256(secretKey)
            )
                .withIssuer(issuer)
                .build()
                .verify(token)
                .subject
        } catch (e: JWTVerificationException) {
            ""
        }
    }

    fun generateTokenCookie(token: String): ResponseCookie = ResponseCookie
        .from(cookieName, token)
        .path("/")
        .maxAge(2000)
        .httpOnly(true)
        .sameSite("strict")
        .secure(true)
        .build()

    fun getCleanCookie(): ResponseCookie = ResponseCookie.from(cookieName).path("/").build()

    fun recoverToken(request: HttpServletRequest): String {
        var token = ""
        request.cookies?.forEach {
            if (it.name.equals(cookieName, true)) {
                token = it.value
            }
        }
        return token
    }

    private fun generateAuthRequestExpiration(): Instant = LocalDateTime
        .now()
        .plusHours(2)
        .toInstant(
            ZoneOffset.of("-03:00")
        )

    private fun generateResetRequestExpiration(): Instant = LocalDateTime
        .now()
        .plusMinutes(3)
        .toInstant(
            ZoneOffset.of("-03:00")
        )
}