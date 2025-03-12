package com.delice.crm.core.user.domain.entities

import java.time.LocalDate
import java.util.UUID

data class User(
    val uuid: UUID?,
    val login: String? = null,
    val pass: String? = null,
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",
    val userType: UserType? = UserType.EMPLOYEE,
    val status: UserStatus? = UserStatus.ACTIVE,
    val avatar: String? = "",
    val document: String? = "",
    val phone: String? = "",
    val dateOfBirth: LocalDate? = LocalDate.now(),
    val state: String? = "",
    val city: String? = "",
    val createdAt: LocalDate? = LocalDate.now(),
    val modifiedAt: LocalDate? = LocalDate.now(),
    val createdBy: String? = "",
    val modifiedBy: String? = "",
)