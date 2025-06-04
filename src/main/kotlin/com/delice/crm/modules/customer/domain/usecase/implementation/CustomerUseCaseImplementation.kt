package com.delice.crm.modules.customer.domain.usecase.implementation

import com.delice.crm.api.economicActivities.domain.entities.EconomicActivity
import com.delice.crm.api.economicActivities.domain.usecase.EconomicActivityUseCase
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.exceptions.*
import com.delice.crm.modules.customer.domain.repository.CustomerRepository
import com.delice.crm.modules.customer.domain.usecase.CustomerUseCase
import com.delice.crm.modules.customer.domain.usecase.response.*
import com.delice.crm.modules.wallet.domain.repository.WalletRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerUseCaseImplementation(
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository,
    private val economicActivityUseCase: EconomicActivityUseCase
) : CustomerUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(CustomerUseCaseImplementation::class.java)
    }

    override fun registerCustomer(customer: Customer, userUUID: UUID): CustomerResponse {
        return try {
            val validate = validateCustomerInfo(customer)

            if (validate.error != null) {
                return validate
            }

            if (customerRepository.getCustomerByDocument(customer.document!!) != null) {
                return CustomerResponse(error = CUSTOMER_ALREADY_EXISTS)
            }

            return CustomerResponse(customer = customerRepository.registerCustomer(customer, userUUID))
        } catch (e: Exception) {
            logger.error("ERROR_REGISTER_CUSTOMER", e)
            CustomerResponse(error = CUSTOMER_UNEXPECTED)
        }
    }

    override fun updateCustomer(customer: Customer, userUUID: UUID): CustomerResponse {
        return try {
            val validate = validateCustomerInfo(customer)

            if (validate.error != null) {
                return validate
            }

            if (customerRepository.getCustomerByUUID(customer.uuid!!) == null) {
                return CustomerResponse(error = CUSTOMER_NOT_FOUND)
            }

            return CustomerResponse(customer = customerRepository.updateCustomer(customer, userUUID))
        } catch (e: Exception) {
            logger.error("ERROR_UPDATE_CUSTOMER", e)
            CustomerResponse(error = CUSTOMER_UNEXPECTED)
        }
    }

    override fun approvalCustomer(status: CustomerStatus, customerUUID: UUID, userUUID: UUID): ApprovalCustomerResponse {
        return try {
            if (customerRepository.getCustomerByUUID(customerUUID) == null) {
                return ApprovalCustomerResponse(error = CUSTOMER_NOT_FOUND)
            }

            val wallet = walletRepository.getCustomerWallet(customerUUID, null)

            if(wallet != null) {
                return ApprovalCustomerResponse(error = CUSTOMER_IN_WALLET)
            }

            customerRepository.approvalCustomer(status, customerUUID, userUUID)

            return ApprovalCustomerResponse(ok = true)
        } catch (e: Exception) {
            logger.error("ERROR_APPROVAL_CUSTOMER", e)
            ApprovalCustomerResponse(error = CUSTOMER_UNEXPECTED)
        }
    }

    override fun listEconomicActivitiesByCustomerUUID(customerUUID: UUID): CustomerEconomicActivities {
        return try {
            if (customerRepository.getCustomerByUUID(customerUUID) == null) {
                return CustomerEconomicActivities(error = CUSTOMER_NOT_FOUND)
            }

            return CustomerEconomicActivities(
                activities = customerRepository.listEconomicActivitiesByCustomerUUID(
                    customerUUID
                )
            )
        } catch (e: Exception) {
            logger.error("ERROR_ECONOMIC_ACTIVITIES_CUSTOMER", e)
            CustomerEconomicActivities(error = CUSTOMER_UNEXPECTED)
        }
    }

    override fun getCustomerByUUID(customerUUID: UUID): CustomerResponse {
        return try {
            val customer = customerRepository.getCustomerByUUID(customerUUID)
                ?: return CustomerResponse(error = CUSTOMER_NOT_FOUND)

            return CustomerResponse(customer = customer)
        } catch (e: Exception) {
            logger.error("ERROR_GET_BY_UUID_CUSTOMER", e)
            CustomerResponse(error = CUSTOMER_UNEXPECTED)
        }
    }

    override fun getCustomerPagination(page: Int, count: Int, params: Map<String, Any?>): CustomerPaginationResponse {
        return try {
            return CustomerPaginationResponse(
                customers = customerRepository.getCustomerPagination(page, count, params),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ERROR_GET_BY_UUID_CUSTOMER", e)
            CustomerPaginationResponse(error = CUSTOMER_UNEXPECTED)
        }
    }

    override fun listSimpleCustomer(): SimpleCustomersResponse {
        return try {
            return SimpleCustomersResponse(
                customers = customerRepository.listSimpleCustomer(),
                error = null
            )
        } catch (e: Exception) {
            logger.error("ERROR_GET_SIMPLE_CUSTOMER", e)
            SimpleCustomersResponse(error = CUSTOMER_UNEXPECTED)
        }
    }

    private fun validateCustomerInfo(customer: Customer): CustomerResponse {
        return when {
            customer.companyName.isNullOrBlank() -> {
                CustomerResponse(error = COMPANY_NAME_IS_EMPTY)
            }

            customer.tradingName.isNullOrBlank() -> {
                CustomerResponse(error = TRADING_NAME_IS_EMPTY)
            }

            customer.personName.isNullOrBlank() -> {
                CustomerResponse(error = PERSON_NAME_IS_EMPTY)
            }

            customer.document.isNullOrBlank() -> {
                CustomerResponse(error = DOCUMENT_MUST_PROVIDED)
            }

            customer.contacts.isNullOrEmpty() -> {
                CustomerResponse(error = CUSTOMER_CONTACTS_IS_EMPTY)
            }

            customer.state.isNullOrBlank() -> {
                CustomerResponse(error = CUSTOMER_STATE_IS_EMPTY)
            }

            customer.city.isNullOrBlank() -> {
                CustomerResponse(error = CUSTOMER_CITY_IS_EMPTY)
            }

            customer.zipCode.isNullOrBlank() -> {
                CustomerResponse(error = CUSTOMER_ZIP_CODE_IS_EMPTY)
            }

            customer.address.isNullOrBlank() -> {
                CustomerResponse(error = CUSTOMER_ADDRESS_IS_EMPTY)
            }

            customer.complement.isNullOrBlank() -> {
                CustomerResponse(error = CUSTOMER_COMPLEMENT_IS_EMPTY)
            }

            customer.addressNumber == 0 -> {
                CustomerResponse(error = CUSTOMER_ADDRESS_NUMBER_IS_EMPTY)
            }

            customer.economicActivitiesCodes.isNullOrEmpty() -> {
                CustomerResponse(error = CUSTOMER_ECONOMIC_ACTIVITIES_IS_EMPTY)
            }

            else -> {
                val economicActivities = ArrayList<EconomicActivity>()
                var error: CustomerExceptions? = null

                customer.economicActivitiesCodes.forEach {
                    val economicActivity = economicActivityUseCase.getEconomicActivity(it)

                    if (economicActivity.error != null) {
                        error = CustomerExceptions(
                            code = economicActivity.error.code,
                            message = economicActivity.error.message,
                        )

                        return@forEach
                    }else{
                        economicActivities.add(economicActivity.economicActivity!!)
                    }
                }

                customer.economicActivities = economicActivities

                if (error != null) {
                    return CustomerResponse(error = error)
                }

                CustomerResponse()
            }
        }
    }
}