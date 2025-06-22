package com.delice.crm.core.user.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class User(
    val uuid: UUID?,
    val login: String? = null,
    val pass: String? = null,
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",
    val userType: UserType? = UserType.EMPLOYEE,
    val status: UserStatus? = UserStatus.FIRST_ACCESS,
    val avatar: String? = "",
    val document: String? = "",
    val phone: String? = "",
    val dateOfBirth: LocalDate? = LocalDate.now(),
    val state: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val address: String? = "",
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

data class SimpleUser(
    val uuid: UUID,
    val login: String,
    val userName: String,
)

class ChangeAvatar(
    val avatar: String
)