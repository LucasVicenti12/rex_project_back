package com.delice.crm.api.preCustomer.domain.usecase.implementation

import com.delice.crm.core.utils.extensions.removeAlphaChars
import com.delice.crm.api.preCustomer.domain.exceptions.INVALID_DOCUMENT
import com.delice.crm.api.preCustomer.domain.exceptions.PRE_CUSTOMER_NOT_FOUND
import com.delice.crm.api.preCustomer.domain.exceptions.PRE_CUSTOMER_UNEXPECTED_ERROR
import com.delice.crm.api.preCustomer.domain.repository.PreCustomerRepository
import com.delice.crm.api.preCustomer.domain.usecase.PreCustomerUseCase
import com.delice.crm.api.preCustomer.domain.usecase.response.PreCustomerResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PreCustomerUseCaseImplementation(
    private val preCustomerRepository: PreCustomerRepository
) : PreCustomerUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(PreCustomerUseCaseImplementation::class.java)
    }

    override fun getPreCustomer(document: String): PreCustomerResponse {
        try {
            val query = document.removeAlphaChars()

            if (query.isBlank() || query.length < 14) {
                return PreCustomerResponse(error = INVALID_DOCUMENT)
            }

            var customer = preCustomerRepository.getPreCustomerInBase(query)

            if (customer == null) {
                customer = preCustomerRepository.getPreCustomerInAPIBase(query)

                if (customer == null) {
                    return PreCustomerResponse(error = PRE_CUSTOMER_NOT_FOUND)
                }

                preCustomerRepository.savePreCustomer(customer)
            }

            return PreCustomerResponse(customer = customer)
        } catch (e: Exception) {
            logger.error("PRE_CUSTOMER_QUERY", e)
            return PreCustomerResponse(error = PRE_CUSTOMER_UNEXPECTED_ERROR)
        }
    }
}