package com.delice.crm.modules.wallet.domain.usecase.response

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.wallet.domain.entities.Wallet
import com.delice.crm.modules.wallet.domain.exceptions.WalletExceptions

data class WalletResponse(
    val wallet: Wallet? = null,
    val error: WalletExceptions? = null
)

data class WalletPaginationResponse(
    val wallet: Pagination<Wallet>? = null,
    val error: WalletExceptions? = null
)