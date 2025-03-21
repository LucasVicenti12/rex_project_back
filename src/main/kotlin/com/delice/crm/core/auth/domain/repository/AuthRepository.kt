package com.delice.crm.core.auth.domain.repository

import com.delice.crm.core.user.domain.entities.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface AuthRepository {
    fun findUserByLogin(login: String): User?
    fun registerUser(user: User): User?
    fun getGrantedAuthorities(user: User): List<GrantedAuthority>
    fun changePassword(userUUID: UUID, newPassword: String)
}