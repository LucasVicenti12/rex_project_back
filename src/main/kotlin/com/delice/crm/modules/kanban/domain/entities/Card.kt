package com.delice.crm.modules.kanban.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.SerializableCustomer
import com.delice.crm.modules.wallet.domain.entities.Wallet
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
    var cardBaseMetadata: CardBaseMetadata? = null,
    var metadata: CardMetadata? = null,
    var tag: Tag? = null,
    var status: CardStatus? = CardStatus.ACTIVE,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
)

@Serializable
data class CardBaseMetadata(
    val customer: SerializableCustomer? = null,
)

class CardMetadata(
    var customer: Customer? = null,
    var wallet: Wallet? = null,
)

enum class CardStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1),
}

const val CARD_MOVE_MESSAGE: String = "The card %s was moved to column %s by %s"
const val CARD_LEAD_TITLE: String = "Customer approval - %s"
const val CARD_CONTENT_EMAIL: String = """
        <table cellpadding="0" cellspacing="0" border="0" width="250" style="font-family: Arial, sans-serif; background-color: #dbe7f0; border-radius: 6px; padding: 10px;">
        <tr>
            <td style="font-size: 16px; font-weight: bold; color: #333333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                %s %s
            </td>
        </tr>
        <tr>
            <td style="font-size: 14px; color: #555555; padding-top: 4px;">
                %s
            </td>
        </tr>
        <tr>
            <td style="font-size: 12px; color: #666666; padding-top: 8px;">
                Changed: %s
            </td>
        </tr>
        <tr>
            <td style="padding-top: 8px;">
                <span style="display: inline-block; background-color: #007bff; color: #ffffff; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold;">
                    %s
                </span>
            </td>
        </tr>
    </table>
"""
