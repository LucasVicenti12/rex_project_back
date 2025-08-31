package com.delice.crm.core.notification.domain.entities

import com.delice.crm.core.user.domain.entities.SimpleUser
import java.time.LocalDateTime
import java.util.UUID

class Notification(
    var uuid: UUID? = null,
    val message: String? = null,
    val title: String? = null,
    val sender: SimpleUser,
    val receivers: List<SimpleUser>? = null,
    val read: Boolean = false,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
)