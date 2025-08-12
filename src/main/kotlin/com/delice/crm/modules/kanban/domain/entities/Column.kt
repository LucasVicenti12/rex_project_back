package com.delice.crm.modules.kanban.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.core.utils.enums.HasType
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

class Column(
    var uuid: UUID? = null,
    var boardUUID: UUID? = null,
    var code: String? = null,
    var title: String? = null,
    var description: String? = null,
    var allowedColumns: List<UUID>? = listOf(),
    var rules: List<ColumnRule>? = listOf(),
    var status: ColumnStatus? = ColumnStatus.ACTIVE,
    var type: ColumnType? = ColumnType.NONE,
    var isDefault: Boolean? = false,
    var withWarnings: Boolean = false,
    var totalCards: Int? = 0,
    var totalValue: Double? = 0.0,
    var index: Int? = 0,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

class ColumnRule(
    var uuid: UUID? = null,
    var columnUUID: UUID? = null,
    var title: String? = null,
    var type: ColumnRuleType? = null,
    var metadata: ColumnRuleMetadata? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

enum class ColumnRuleType(override val type: String): HasType{
    SEND_EMAIL("SEND_EMAIL"),
    NOTIFY_USER("NOTIFY_USER"),
    ADD_TAG("ADD_TAG"),
    REMOVE_TAG("REMOVE_TAG"),
    VALIDATE_CUSTOMER("VALIDATE_CUSTOMER"),
    VALIDATE_CUSTOMER_WALLET("VALIDATE_CUSTOMER_WALLET"),
    REVIEW_CUSTOMER("REVIEW_CUSTOMER"),
    REPROVE_CUSTOMER("REPROVE_CUSTOMER"),
    NOT_MOVABLE("NOT_MOVABLE"),
    APPROVE_CUSTOMER("APPROVE_CUSTOMER");
}

@Serializable
class ColumnRuleMetadata(
    var emails: List<String>? = listOf(),
    var notifyUsers: List<String>? = listOf(),
    var tag: String? = null,
)

enum class ColumnStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1),
}

enum class ColumnType(override val type: String) : HasType {
    COUNTER("COUNTER"),
    VALUE("VALUE"),
    NONE("NONE");
}

enum class KanbanKeys(override val type: String): HasType{
    LEADS("LEADS")
}