package com.delice.crm.modules.wallet.domain.usecase

import com.delice.crm.modules.wallet.domain.entities.Wallet
import com.delice.crm.modules.wallet.domain.usecase.response.WalletPaginationResponse
import com.delice.crm.modules.wallet.domain.usecase.response.WalletResponse
import java.util.UUID

interface WalletUseCase {
    fun createWallet(wallet: Wallet, userUUID: UUID): WalletResponse
    fun updateWallet(wallet: Wallet, userUUID: UUID): WalletResponse
    fun getWalletByUUID(walletUUID: UUID): WalletResponse
    fun getWalletPagination(count: Int, page: Int, params: Map<String, Any?>): WalletPaginationResponse
}