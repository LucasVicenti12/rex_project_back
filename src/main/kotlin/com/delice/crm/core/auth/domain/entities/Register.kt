package com.delice.crm.core.auth.domain.entities

import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType

class Register(
    val login: String? = null,
    val password: String? = null,
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",
    val userType: UserType? = UserType.EMPLOYEE,
    val status: UserStatus? = UserStatus.ACTIVE,
    val avatar: String? = "",
    val document: String? = ""
)