package com.delice.crm.core.notification.domain.usecase

import com.delice.crm.core.notification.domain.usecase.response.NotificationListResponse
import com.delice.crm.core.notification.domain.usecase.response.NotificationResponse
import java.util.*

interface NotificationUseCase {
    fun listMyNotifications(params: Map<String, Any?>): NotificationListResponse
    fun markNotification(notificationUUID: UUID): NotificationResponse
    fun markAllNotifications(): NotificationListResponse
}