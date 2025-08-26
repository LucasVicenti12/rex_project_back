package com.delice.crm.core.notification.infra.database

import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.filter.ExposedFilter
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.datetime

object NotificationDatabase : Table("notification") {
    var uuid = uuid("uuid").uniqueIndex()
    var message = text("message").nullable()
    var title = varchar("title", 90)
    var sender = uuid("sender") references UserDatabase.uuid

    var createdAt = datetime("created_at")
}

object NotificationReceiversDatabase : Table("notification_receivers") {
    var notificationUUID = uuid("notification_uuid") references NotificationDatabase.uuid
    var receiverUUID = uuid("receiver_uuid") references UserDatabase.uuid
    var read = bool("read").default(false)
}

data class NotificationReceiversFilter(
    val parameters: Map<String, Any?>,
) : ExposedFilter<NotificationReceiversDatabase> {
    override fun toFilter(table: NotificationReceiversDatabase): Op<Boolean> {
        var op: Op<Boolean> = Op.TRUE

        if (parameters.isEmpty()) {
            return op
        }

        parameters["isRead"]?.let {
            op = if (it.toString().toBoolean()) {
                op.and(table.read eq true)
            }else{
                op.and(table.read eq false)
            }
        }

        return op
    }
}