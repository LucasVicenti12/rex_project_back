package com.delice.crm.core.notification.domain.repository

import com.delice.crm.core.notification.domain.entities.Notification
import java.util.UUID

interface NotificationRepository {
    fun listMyNotifications(userUUID: UUID, params: Map<String, Any?>): List<Notification>?
    fun markNotification(notificationUUID: UUID): Notification?
    fun markAllNotifications(userUUID: UUID): List<Notification>?
    fun getNotificationByUUID(notificationUUID: UUID): Notification?
    fun createNotification(notification: Notification)
}