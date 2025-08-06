package com.delice.crm.modules.kanban.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.modules.customer.domain.entities.SerializableCustomer
import com.delice.crm.modules.wallet.domain.entities.SerializableWallet
import java.time.LocalDateTime
import java.util.*

import kotlinx.serialization.Serializable

class Card(
    var uuid: UUID? = null,
    var boardUUID: UUID? = null,
    var columnUUID: UUID? = null,
    var code: String? = null,
    var title: String? = null,
    var description: String? = null,
    var movable: Boolean = false,
    var metadata: CardMetadata? = null,
    var tag: Tag? = null,
    var warning: CardWarning? = null,
    var status: CardStatus? = CardStatus.ACTIVE,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

class CardWarning(
    var description: String? = null,
)

@Serializable
data class CardMetadata(
    val customer: SerializableCustomer? = null,
    val wallet: SerializableWallet? = null,
)

enum class CardStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1),
}