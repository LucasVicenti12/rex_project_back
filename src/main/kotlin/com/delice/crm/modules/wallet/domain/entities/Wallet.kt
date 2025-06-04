package com.delice.crm.modules.wallet.domain.entities

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.modules.customer.domain.entities.Customer
import java.time.LocalDateTime
import java.util.UUID

class Wallet(
    val uuid: UUID? = null,
    val label: String? = null,
    var accountable: User? = null,
    var customers: List<Customer>? = emptyList(),
    val observation: String? = "",
    val status: WalletStatus? = WalletStatus.ACTIVE,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val modifiedAt: LocalDateTime? = LocalDateTime.now(),
    val createdBy: UUID? = null,
    val modifiedBy: UUID? = null,
)