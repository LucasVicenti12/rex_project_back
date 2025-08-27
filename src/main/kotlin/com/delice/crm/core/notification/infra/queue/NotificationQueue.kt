package com.delice.crm.core.notification.infra.queue

import com.delice.crm.core.notification.domain.entities.Notification
import com.delice.crm.core.notification.infra.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.LinkedList

@Component
class NotificationQueue(
    private val service: NotificationService,
    private val list: LinkedList<Notification>
) : Thread() {
    companion object {
        private val logger = LoggerFactory.getLogger(NotificationQueue::class.java)
    }

    init {
        start()
    }

    fun addNotification(notification: Notification) {
        list.add(notification)
    }

    override fun run() {
        while (true) {
            try {
                var mail: Notification?

                synchronized(list) {
                    mail = list.poll() ?: null
                }

                if (mail != null) {
                    service.publish(mail!!)
                }
            } catch (e: Exception) {
                logger.error("ERROR ON NOTIFICATION QUEUE -> ${e.message}", e)
            }
        }
    }
}