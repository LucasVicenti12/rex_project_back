package com.delice.crm.core.config.entities

import com.delice.crm.core.user.domain.entities.User
import org.springframework.security.core.GrantedAuthority
import java.util.UUID
import org.springframework.security.core.userdetails.User as SpringUser

class SystemUser(
    private val user: User,
    authorities: List<GrantedAuthority>
) : SpringUser(
    user.login,
    user.pass,
    true,
    true,
    true,
    true,
    authorities
) {
    fun getUserData(): User = user.copy(pass = null)

    val uuid: UUID
        get() = user.uuid!!
}