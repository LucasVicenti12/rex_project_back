package com.delice.crm.core.config.service

import com.delice.crm.core.auth.domain.repository.AuthRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var authRepository: AuthRepository

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = tokenService.recoverToken(request)

        if(token.isNotBlank()) {
            val login = tokenService.validate(token)
            val user = authRepository.findUserByLogin(login)

            val authentication = UsernamePasswordAuthenticationToken(user, null, user!!.authorities)

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}