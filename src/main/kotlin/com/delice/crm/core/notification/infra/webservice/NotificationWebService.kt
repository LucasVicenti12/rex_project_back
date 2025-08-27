package com.delice.crm.core.notification.infra.webservice

import com.delice.crm.core.notification.domain.usecase.NotificationUseCase
import com.delice.crm.core.notification.domain.usecase.response.NotificationListResponse
import com.delice.crm.core.notification.domain.usecase.response.NotificationResponse
import com.delice.crm.core.utils.filter.parametersToMap
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/notification")
class NotificationWebService(
    private val notificationUseCase: NotificationUseCase
) {
    @GetMapping("/list")
    fun listMyNotifications(
        request: HttpServletRequest
    ): ResponseEntity<NotificationListResponse> {
        val params = request.queryString.parametersToMap()

        val response = notificationUseCase.listMyNotifications(params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/mark/{notificationUUID}")
    fun markNotification(
        @PathVariable(
            value = "notificationUUID",
            required = true
        ) notificationUUID: UUID
    ): ResponseEntity<NotificationResponse> {
        val response = notificationUseCase.markNotification(notificationUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/markAll")
    fun markAllNotifications(): ResponseEntity<NotificationListResponse> {
        val response = notificationUseCase.markAllNotifications()

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
}