package com.delice.crm.core.notification.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val NOTIFICATION_UNEXPECTED = NotificationExceptions("NOTIFICATION_UNEXPECTED", "An unexpected error has occurred")
val NOTIFICATION_NOT_FOUND = NotificationExceptions("NOTIFICATION_NOT_FOUND", "Notification not found")

class NotificationExceptions(code: String, message: String) : DefaultError(code, message)