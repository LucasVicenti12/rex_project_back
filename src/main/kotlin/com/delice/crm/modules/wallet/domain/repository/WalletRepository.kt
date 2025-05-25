package com.delice.crm.modules.wallet.domain.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.wallet.domain.entities.Wallet
import java.util.*

interface WalletRepository {
    fun createWallet(wallet: Wallet, userUUID: UUID): Wallet?
    fun updateWallet(wallet: Wallet, userUUID: UUID): Wallet?
    fun getWalletByUUID(walletUUID: UUID): Wallet?
    fun getUserWalletByUUID(userUUID: UUID): Wallet?
    fun getWalletPagination(count: Int, page: Int, params: Map<String, Any?>): Pagination<Wallet>
}