package com.delice.crm.core.auth.domain.entities

import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType
import java.time.LocalDate

class Register(
    val login: String? = null,
    val password: String? = null,
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",
    val userType: UserType? = UserType.EMPLOYEE,
    val status: UserStatus? = UserStatus.FIRST_ACCESS,
    val avatar: String? = "",
    val document: String? = "",
    val phone: String? = "",
    val dateOfBirth: LocalDate? = null,
    val state: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val address: String? = "",
)