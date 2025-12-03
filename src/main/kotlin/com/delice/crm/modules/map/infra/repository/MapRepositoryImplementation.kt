package com.delice.crm.modules.map.infra.repository
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.map.domain.entities.MapCustomerForState
import com.delice.crm.modules.map.domain.repository.MapRepository
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class MapRepositoryImplementation : MapRepository {
    override fun getMapCustomerForState(): List<MapCustomerForState>? = transaction {
        val stateColumn = CustomerDatabase.state
        val customerCount = CustomerDatabase.uuid.count()

        CustomerDatabase
            .select(stateColumn, customerCount)
            .groupBy(stateColumn)
            .map {
                MapCustomerForState(
                    state = it[stateColumn] ?: "N/A",
                    customer = it[customerCount].toInt()
                )
            }
            .takeIf { it.isNotEmpty() }
    }
}