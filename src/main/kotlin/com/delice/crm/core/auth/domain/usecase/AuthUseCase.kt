package com.delice.crm.core.auth.domain.usecase

import com.delice.crm.core.auth.domain.entities.Login
import com.delice.crm.core.auth.domain.entities.Register
import com.delice.crm.core.auth.domain.entities.ResetPassword
import com.delice.crm.core.auth.domain.usecase.response.*
import com.delice.crm.core.user.domain.entities.User
import org.springframework.security.core.GrantedAuthority
import java.util.UUID

interface AuthUseCase {
    fun registerUser(register: Register): RegisterResponse
    fun loginUser(login: Login): LoginResponse
    fun getGrantedAuthorities(user: User): List<GrantedAuthority>
    fun getAuthenticated(useUUID: UUID): AuthenticatedResponse
    fun findUserByLogin(login: String): User?
    fun forgotPassword(email: String, host: String): ForgotPasswordResponse
    fun resetPassword(userUUID: UUID, resetPassword: ResetPassword): ChangePasswordResponse
    fun resetPasswordWithToken(resetPassword: ResetPassword, token: String): ChangePasswordResponse
}