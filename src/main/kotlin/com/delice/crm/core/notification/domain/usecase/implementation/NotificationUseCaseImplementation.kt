package com.delice.crm.core.notification.domain.usecase.implementation

import com.delice.crm.core.notification.domain.exceptions.NOTIFICATION_NOT_FOUND
import com.delice.crm.core.notification.domain.exceptions.NOTIFICATION_UNEXPECTED
import com.delice.crm.core.notification.domain.repository.NotificationRepository
import com.delice.crm.core.notification.domain.usecase.NotificationUseCase
import com.delice.crm.core.notification.domain.usecase.response.NotificationListResponse
import com.delice.crm.core.notification.domain.usecase.response.NotificationResponse
import com.delice.crm.core.utils.function.getCurrentUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class NotificationUseCaseImplementation(
    private val notificationRepository: NotificationRepository
) : NotificationUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(NotificationUseCaseImplementation::class.java)
    }

    override fun listMyNotifications(params: Map<String, Any?>): NotificationListResponse = try {
        val currentUser = getCurrentUser()

        NotificationListResponse(
            notifications = notificationRepository.listMyNotifications(
                userUUID = currentUser.uuid,
                params = params,
            )
        )
    } catch (e: Exception) {
        logger.error("ERROR_ON_LIST_MY_NOTIFICATIONS", e)
        NotificationListResponse(error = NOTIFICATION_UNEXPECTED)
    }

    override fun markNotification(notificationUUID: UUID): NotificationResponse = try {
        val notification = notificationRepository.getNotificationByUUID(notificationUUID)

        if (notification == null) {
            NotificationResponse(error = NOTIFICATION_NOT_FOUND)
        } else {
            NotificationResponse(
                notification = notificationRepository.markNotification(notificationUUID)
            )
        }
    } catch (e: Exception) {
        logger.error("ERROR_ON_MARK_NOTIFICATION", e)
        NotificationResponse(error = NOTIFICATION_UNEXPECTED)
    }

    override fun markAllNotifications(): NotificationListResponse = try {
        val currentUser = getCurrentUser()

        NotificationListResponse(
            notifications = notificationRepository.markAllNotifications(
                userUUID = currentUser.uuid,
            )
        )
    } catch (e: Exception) {
        logger.error("ERROR_ON_MARK_ALL_NOTIFICATIONS", e)
        NotificationListResponse(error = NOTIFICATION_UNEXPECTED)
    }
}