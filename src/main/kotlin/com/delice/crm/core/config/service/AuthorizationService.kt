package com.delice.crm.core.config.service

import com.delice.crm.core.auth.domain.repository.AuthRepository
import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.config.entities.SystemUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    val authRepository: AuthRepository,
    val authUseCase: AuthUseCase
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        return authRepository.findUserByLogin(username)?.run {
            SystemUser(this, authUseCase.getGrantedAuthorities(this))
        }
    }
}