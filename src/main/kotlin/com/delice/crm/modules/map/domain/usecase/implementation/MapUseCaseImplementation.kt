package com.delice.crm.modules.map.domain.usecase.implementation
import com.delice.crm.modules.map.domain.exceptions.MAP_NOT_FOUND
import com.delice.crm.modules.map.domain.exceptions.MAP_UNEXPECTED_ERROR
import com.delice.crm.modules.map.domain.repository.MapRepository
import com.delice.crm.modules.map.domain.usecase.MapUseCase
import com.delice.crm.modules.map.domain.usecase.response.MapResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MapUseCaseImplementation (
    private val mapRepository: MapRepository
) : MapUseCase {
    companion object{
        private val logger = LoggerFactory.getLogger(MapUseCaseImplementation::class.java)
    }

    override fun getMapCustomerForState(): MapResponse = try {
        val mapCustomerForState = mapRepository.getMapCustomerForState()

        if(mapCustomerForState == null) {
            MapResponse(error = MAP_NOT_FOUND)
        } else {
            MapResponse(customersByState = mapCustomerForState)
        }
    } catch (e: Exception) {
        logger.error("GET_MAP_CUSTOMER_FOR_STATE", e)
        MapResponse(error = MAP_UNEXPECTED_ERROR)
    }
}
