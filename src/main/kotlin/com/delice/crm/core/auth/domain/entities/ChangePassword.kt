package com.delice.crm.core.auth.domain.entities

data class ChangePassword(
    val newPassword: String? = "",
    val currentPassword: String? = ""
)