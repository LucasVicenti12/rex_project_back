package com.delice.crm.modules.wallet.domain.usecase.implementation

import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.wallet.domain.entities.Wallet
import com.delice.crm.modules.wallet.domain.exceptions.*
import com.delice.crm.modules.wallet.domain.repository.WalletRepository
import com.delice.crm.modules.wallet.domain.usecase.WalletUseCase
import com.delice.crm.modules.wallet.domain.usecase.response.FreeCustomers
import com.delice.crm.modules.wallet.domain.usecase.response.WalletPaginationResponse
import com.delice.crm.modules.wallet.domain.usecase.response.WalletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class WalletUseCaseImplementation(
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository
) : WalletUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(WalletUseCaseImplementation::class.java)
    }

    override fun createWallet(wallet: Wallet, userUUID: UUID): WalletResponse = try {
        when {
            userRepository.getUserByUUID(wallet.accountable!!.uuid!!) == null -> {
                WalletResponse(error = WALLET_USER_NOT_FOUND)
            }

            wallet.customers.isNullOrEmpty() -> {
                WalletResponse(error = WALLET_CUSTOMER_IS_EMPTY)
            }

            wallet.label.isNullOrEmpty() -> {
                WalletResponse(error = WALLET_LABEL_IS_EMPTY)
            }

            validateRepeatedCustomers(wallet.customers!!) -> {
                WalletResponse(error = WALLET_CUSTOMER_DUPLICATE)
            }

            else -> {
                var error: WalletExceptions? = null

                wallet.customers!!.forEach {
                    val customer = customerRepository.getCustomerByUUID(it.uuid!!)

                    if (customer == null) {
                        error = WALLET_CUSTOMER_NOT_FOUND
                        return@forEach
                    }

                    val attached = walletRepository.getCustomerWallet(customer.uuid!!, null)

                    if (attached != null) {
                        error = WALLET_CUSTOMER_ALREADY_ATTACHED
                        return@forEach
                    }
                }

                if (error != null) {
                    WalletResponse(error = error)
                } else {
                    WalletResponse(wallet = walletRepository.createWallet(wallet, userUUID))
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_CREATE_WALLET", e)
        WalletResponse(error = WALLET_UNEXPECTED_ERROR)
    }

    override fun updateWallet(wallet: Wallet, userUUID: UUID): WalletResponse = try {
        when {
            walletRepository.getWalletByUUID(wallet.uuid!!) == null -> {
                WalletResponse(error = WALLET_NOT_FOUND)
            }

            userRepository.getUserByUUID(wallet.accountable!!.uuid!!) == null -> {
                WalletResponse(error = WALLET_USER_NOT_FOUND)
            }

            wallet.customers.isNullOrEmpty() -> {
                WalletResponse(error = WALLET_CUSTOMER_IS_EMPTY)
            }

            wallet.label.isNullOrEmpty() -> {
                WalletResponse(error = WALLET_LABEL_IS_EMPTY)
            }

            validateRepeatedCustomers(wallet.customers!!) -> {
                WalletResponse(error = WALLET_CUSTOMER_DUPLICATE)
            }

            else -> {
                var error: WalletExceptions? = null

                wallet.customers!!.forEach {
                    val customer = customerRepository.getCustomerByUUID(it.uuid!!)

                    if (customer == null) {
                        error = WALLET_CUSTOMER_NOT_FOUND
                        return@forEach
                    }

                    val attached = walletRepository.getCustomerWallet(customer.uuid!!, wallet.uuid)

                    if (attached != null) {
                        error = WALLET_CUSTOMER_ALREADY_ATTACHED
                        return@forEach
                    }
                }

                if (error != null) {
                    WalletResponse(error = error)
                } else {
                    WalletResponse(wallet = walletRepository.updateWallet(wallet, userUUID))
                }
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_UPDATE_WALLET", e)
        WalletResponse(error = WALLET_UNEXPECTED_ERROR)
    }

    override fun getWalletByUUID(walletUUID: UUID): WalletResponse {
        return try {
            val wallet = walletRepository.getWalletByUUID(walletUUID) ?: return WalletResponse(error = WALLET_NOT_FOUND)

            WalletResponse(wallet = wallet)
        } catch (e: Exception) {
            logger.error("ERROR_GET_WALLET_BY_UUID", e)
            WalletResponse(error = WALLET_UNEXPECTED_ERROR)
        }
    }

    override fun getWalletPagination(count: Int, page: Int, orderBy: OrderBy?, params: Map<String, Any?>): WalletPaginationResponse {
        return try {
            return WalletPaginationResponse(
                wallet = walletRepository.getWalletPagination(count, page, orderBy, params),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ERROR_GET_WALLET_PAGINATION", e)
            WalletPaginationResponse(error = WALLET_UNEXPECTED_ERROR)
        }
    }

    override fun getFreeCustomers(): FreeCustomers {
        return try {
            return FreeCustomers(
                customers = walletRepository.getFreeCustomers(),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ERROR_GET_FREE_CUSTOMERS", e)
            FreeCustomers(error = WALLET_UNEXPECTED_ERROR)
        }
    }

    private fun validateRepeatedCustomers(customers: List<Customer>): Boolean {
        return (customers.groupBy { it.uuid }.filter { it.value.size > 1 }.keys).isNotEmpty()
    }
}