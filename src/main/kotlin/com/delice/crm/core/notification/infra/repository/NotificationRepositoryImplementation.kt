package com.delice.crm.core.notification.infra.repository

import com.delice.crm.core.notification.domain.entities.Notification
import com.delice.crm.core.notification.domain.repository.NotificationRepository
import com.delice.crm.core.notification.infra.database.NotificationDatabase
import com.delice.crm.core.notification.infra.database.NotificationReceiversDatabase
import com.delice.crm.core.notification.infra.database.NotificationReceiversFilter
import com.delice.crm.core.notification.infra.queue.NotificationQueue
import com.delice.crm.core.user.domain.entities.SimpleUser
import com.delice.crm.core.user.infra.database.UserDatabase
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class NotificationRepositoryImplementation(
    private val notificationQueue: NotificationQueue
) : NotificationRepository {
    override fun listMyNotifications(userUUID: UUID, params: Map<String, Any?>): List<Notification>? = transaction {
        NotificationReceiversDatabase
            .join(
                otherTable = NotificationDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { NotificationDatabase.uuid eq NotificationReceiversDatabase.notificationUUID }
            )
            .join(
                otherTable = UserDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { UserDatabase.uuid eq NotificationDatabase.sender }
            )
            .select(
                NotificationDatabase.uuid,
                NotificationDatabase.message,
                NotificationDatabase.title,
                UserDatabase.uuid,
                UserDatabase.login,
                UserDatabase.name,
                UserDatabase.surname,
                NotificationReceiversDatabase.read,
                NotificationDatabase.createdAt,
            )
            .orderBy(
                column = NotificationDatabase.createdAt,
                order = SortOrder.DESC
            )
            .where {
                NotificationReceiversFilter(params).toFilter(NotificationReceiversDatabase) and (
                        NotificationReceiversDatabase.receiverUUID eq userUUID
                        )
            }.map {
                resultRowToNotification(it)
            }
    }

    override fun createNotification(notification: Notification) {
        transaction {
            notification.uuid = UUID.randomUUID()

            NotificationDatabase.insert {
                it[uuid] = notification.uuid!!
                it[message] = notification.message!!
                it[title] = notification.title!!
                it[sender] = notification.sender.uuid
                it[createdAt] = notification.createdAt!!
            }

            if (!notification.receivers.isNullOrEmpty()) {
                notification.receivers.forEach { receive ->
                    NotificationReceiversDatabase.insert {
                        it[notificationUUID] = notification.uuid!!
                        it[receiverUUID] = receive.uuid
                    }
                }
            }
            notificationQueue.addNotification(notification)
        }
    }

    override fun markNotification(notificationUUID: UUID): Notification? = transaction {
        NotificationReceiversDatabase.update({
            NotificationReceiversDatabase.notificationUUID eq notificationUUID
        }) {
            it[read] = true
        }

        return@transaction getNotificationByUUID(notificationUUID)
    }

    override fun markAllNotifications(userUUID: UUID): List<Notification>? = transaction {
        NotificationReceiversDatabase.update({
            NotificationReceiversDatabase.receiverUUID eq userUUID
        }) {
            it[read] = true
        }

        return@transaction listMyNotifications(
            userUUID = userUUID,
            params = mapOf()
        )
    }

    override fun getNotificationByUUID(notificationUUID: UUID): Notification? = transaction {
        NotificationReceiversDatabase
            .join(
                otherTable = NotificationDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { NotificationDatabase.uuid eq NotificationReceiversDatabase.notificationUUID }
            )
            .join(
                otherTable = UserDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { UserDatabase.uuid eq NotificationDatabase.sender }
            )
            .select(
                NotificationDatabase.uuid,
                NotificationDatabase.message,
                NotificationDatabase.title,
                UserDatabase.uuid,
                UserDatabase.login,
                UserDatabase.name,
                UserDatabase.surname,
                NotificationReceiversDatabase.read,
                NotificationDatabase.createdAt,
            ).where {
                NotificationReceiversDatabase.notificationUUID eq notificationUUID
            }.map {
                resultRowToNotification(it)
            }.firstOrNull()
    }

    private fun resultRowToNotification(it: ResultRow): Notification = Notification(
        uuid = it[NotificationDatabase.uuid],
        message = it[NotificationDatabase.message],
        title = it[NotificationDatabase.title],
        sender = SimpleUser(
            uuid = it[UserDatabase.uuid],
            login = it[UserDatabase.login],
            userName = "${it[UserDatabase.name]} ${it[UserDatabase.surname]}",
        ),
        read = it[NotificationReceiversDatabase.read],
        createdAt = it[NotificationDatabase.createdAt],
    )
}