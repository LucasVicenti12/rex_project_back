package com.delice.crm.core.notification.infra.service

import com.delice.crm.core.notification.domain.entities.Notification
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val simp: SimpMessagingTemplate
) {
    fun publish(notification: Notification) {
        if (notification.receivers.isNullOrEmpty()) {
            simp.convertAndSend("/topic/notifications", notification)
        } else {
            notification.receivers.forEach {
                simp.convertAndSendToUser(it.login, "/private/notifications", notification)
            }
        }
    }
}