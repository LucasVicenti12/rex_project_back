package com.delice.crm.core.config.entities

import com.delice.crm.core.user.domain.entities.User
import java.util.UUID
import org.springframework.security.core.userdetails.User as SpringUser

class SystemUser(
    private val user: User
) : SpringUser(
    user.login,
    user.pass,
    true,
    true,
    true,
    true,
    user.authorities
) {
    fun getUserData(): User = user.copy(pass = null)

    val uuid: UUID
        get() = user.uuid!!
}