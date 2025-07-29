package com.delice.crm.modules.wallet.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val WALLET_UNEXPECTED_ERROR = WalletExceptions("WALLET_UNEXPECTED_ERROR", "An unexpected error has occurred")
val WALLET_NOT_FOUND = WalletExceptions("WALLET_NOT_FOUND", "The wallet does not exist")
val WALLET_USER_NOT_FOUND = WalletExceptions("WALLET_USER_NOT_FOUND", "This user does not exist")
val WALLET_CUSTOMER_NOT_FOUND = WalletExceptions("WALLET_CUSTOMER_NOT_FOUND", "Customer not found")
val WALLET_CUSTOMER_IS_EMPTY = WalletExceptions("WALLET_CUSTOMER_IS_EMPTY", "At least one customer must be provided")
val WALLET_LABEL_IS_EMPTY = WalletExceptions("WALLET_LABEL_IS_EMPTY", "The wallet label must be provided")
val WALLET_CUSTOMER_ALREADY_ATTACHED = WalletExceptions("WALLET_CUSTOMER_ALREADY_ATTACHED", "This customer already attached in another wallet")
val WALLET_CUSTOMER_DUPLICATE = WalletExceptions("WALLET_CUSTOMER_DUPLICATE", "The customer was selected more than once")

class WalletExceptions(code: String, message: String) : DefaultError(code, message)