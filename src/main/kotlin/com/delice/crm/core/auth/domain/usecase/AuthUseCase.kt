package com.delice.crm.core.auth.domain.usecase

import com.delice.crm.core.auth.domain.entities.Login
import com.delice.crm.core.auth.domain.entities.Register
import com.delice.crm.core.auth.domain.usecase.response.LoginResponse
import com.delice.crm.core.auth.domain.usecase.response.RegisterResponse
import com.delice.crm.core.user.domain.entities.User
import org.springframework.security.core.GrantedAuthority

interface AuthUseCase {
    fun registerUser(register: Register): RegisterResponse
    fun loginUser(login: Login): LoginResponse
    fun getGrantedAuthorities(user: User): List<GrantedAuthority>
}