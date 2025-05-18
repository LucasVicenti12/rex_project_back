package com.delice.crm.integrations.viaCep.domain.usecase.implementation

import com.delice.crm.core.utils.extensions.removeAlphaChars
import com.delice.crm.core.utils.extensions.removeSpecialChars
import com.delice.crm.integrations.viaCep.domain.exceptions.ADDRESS_NOT_FOUND
import com.delice.crm.integrations.viaCep.domain.exceptions.ADDRESS_UNEXPECTED_ERROR
import com.delice.crm.integrations.viaCep.domain.exceptions.INVALID_ZIP_CODE
import com.delice.crm.integrations.viaCep.domain.repository.ViaCepRepository
import com.delice.crm.integrations.viaCep.domain.usecase.ViaCepUseCase
import com.delice.crm.integrations.viaCep.domain.usecase.response.ViaCepAddressResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ViaCepUseCaseImplementation(
    private val viaCepRepository: ViaCepRepository
) : ViaCepUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(ViaCepUseCaseImplementation::class.java)
    }

    override fun getAddressInViaCepBase(zipCode: String): ViaCepAddressResponse {
        try {
            val query = zipCode.removeAlphaChars()

            if(query.isBlank() || query.length < 8) {
                return ViaCepAddressResponse(error = INVALID_ZIP_CODE)
            }

            var address = viaCepRepository.getAddressInBase(query)

            if (address == null) {
                address = viaCepRepository.getAddressInViaCepBase(query)

                if (address == null) {
                    return ViaCepAddressResponse(error = ADDRESS_NOT_FOUND)
                }

                viaCepRepository.saveAddressInBase(address)
            }

            return ViaCepAddressResponse(address = address)
        } catch (e: Exception) {
            logger.error("USER_MODULE_GET_BY_UUID", e)
            return ViaCepAddressResponse(error = ADDRESS_UNEXPECTED_ERROR)
        }
    }
}