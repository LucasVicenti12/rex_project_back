package com.delice.crm.core.utils.function

import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.config.service.TokenService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

@Autowired
private lateinit var authUseCase: AuthUseCase

@Autowired
private lateinit var tokenService: TokenService

fun getUserRequest(request: HttpServletRequest): UUID? {
    val token = tokenService.recoverToken(request)
    val login = tokenService.validate(token)
    val user = authUseCase.findUserByLogin(login) ?: return null

    return user.uuid
}