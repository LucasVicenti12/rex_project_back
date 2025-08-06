package com.delice.crm.modules.wallet.domain.repository

import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.SimpleCustomer
import com.delice.crm.modules.wallet.domain.entities.Wallet
import java.util.*

interface WalletRepository {
    fun createWallet(wallet: Wallet, userUUID: UUID): Wallet?
    fun updateWallet(wallet: Wallet, userUUID: UUID): Wallet?
    fun getWalletByUUID(walletUUID: UUID): Wallet?
    fun getUserWalletByUUID(userUUID: UUID): Wallet?
    fun getCustomerWallet(customerUUID: UUID, walletUUID: UUID?): Wallet?
    fun getWalletPagination(count: Int, page: Int, params: Map<String, Any?>): Pagination<Wallet>
    fun getFreeCustomers(): List<SimpleCustomer>?
}