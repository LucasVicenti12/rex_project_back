package com.delice.crm.modules.wallet.domain.entities

import com.delice.crm.core.utils.enums.HasCode

enum class WalletStatus(override val code: Int) : HasCode {
    ACTIVE(0),
    INACTIVE(1)
}