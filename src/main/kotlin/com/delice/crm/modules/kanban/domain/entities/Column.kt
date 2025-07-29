package com.delice.crm.modules.kanban.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.core.utils.enums.HasType
import java.time.LocalDateTime
import java.util.UUID

class Column(
    var uuid: UUID? = null,
    var boardUUID: UUID? = null,
    var code: String? = null,
    var title: String? = null,
    var description: String? = null,
    var allowedColumns: List<UUID>? = listOf(),
    var status: ColumnStatus? = ColumnStatus.ACTIVE,
    var type: ColumnType? = ColumnType.NONE,
    var withWarnings: Boolean = false,
    var totalCards: Int? = 0,
    var totalValue: Double? = 0.0,
    var index: Int? = 0,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
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