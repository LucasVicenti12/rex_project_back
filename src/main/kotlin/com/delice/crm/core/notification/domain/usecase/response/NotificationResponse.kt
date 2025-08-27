package com.delice.crm.core.notification.domain.usecase.response

import com.delice.crm.core.notification.domain.entities.Notification
import com.delice.crm.core.notification.domain.exceptions.NotificationExceptions

data class NotificationResponse(
    val notification: Notification? = null,
    val error: NotificationExceptions? = null
)

data class NotificationListResponse(
    val notifications: List<Notification>? = emptyList(),
    val error: NotificationExceptions? = null
)