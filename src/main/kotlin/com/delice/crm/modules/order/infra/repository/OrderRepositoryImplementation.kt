package com.delice.crm.modules.order.infra.repository

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.order.domain.entities.*
import com.delice.crm.modules.order.domain.repository.OrderRepository
import com.delice.crm.modules.order.infra.database.OrderDatabase
import com.delice.crm.modules.order.infra.database.OrderFilter
import com.delice.crm.modules.order.infra.database.OrderItemDatabase
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductStatus
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.product.infra.database.ProductFilter
import com.delice.crm.modules.product.infra.database.ProductOrderBy
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.ceil

@Service
class OrderRepositoryImplementation : OrderRepository {
    override fun createOrder(order: Order): Order? = transaction {
        val newOrderUUID = UUID.randomUUID()

        OrderDatabase.insert {
            it[uuid] = newOrderUUID
            it[discount] = order.discount!!
            it[defaultDiscount] = order.defaultDiscount!!
            it[customerUUID] = order.customer!!.uuid!!
            it[status] = order.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
            it[operatorUUID] = order.operator!!.uuid!!
        }

        return@transaction getOrderByUUID(newOrderUUID)
    }

    override fun changeOrderDefaultDiscount(orderUUID: UUID, manipulateOrder: ManipulateOrder): Order? = transaction {
        OrderDatabase.update({ OrderDatabase.uuid eq orderUUID }) {
            it[defaultDiscount] = manipulateOrder.discount
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getOrderByUUID(orderUUID)
    }

    override fun addOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): List<OrderItem>? =
        transaction {
            val item = getOrderItemByProductUUID(orderUUID, manipulateOrderItem.productUUID)

            if (item == null) {
                OrderItemDatabase.insert {
                    it[OrderItemDatabase.orderUUID] = orderUUID
                    it[quantity] = manipulateOrderItem.quantity
                    it[discount] = manipulateOrderItem.discount
                    it[productUUID] = manipulateOrderItem.productUUID
                    it[createdAt] = LocalDateTime.now()
                    it[modifiedAt] = LocalDateTime.now()
                }
            } else {
                OrderItemDatabase.update({
                    OrderItemDatabase.productUUID eq manipulateOrderItem.productUUID and
                            (OrderItemDatabase.orderUUID eq orderUUID)
                }) {
                    it[quantity] = manipulateOrderItem.quantity
                    it[discount] = manipulateOrderItem.discount
                    it[modifiedAt] = LocalDateTime.now()
                }
            }

            return@transaction getOrderItemsByUUID(orderUUID)
        }

    override fun changeOrderItemDiscount(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): List<OrderItem>? =
        transaction {
            OrderItemDatabase.update({
                OrderItemDatabase.productUUID eq manipulateOrderItem.productUUID and
                        (OrderItemDatabase.orderUUID eq orderUUID)
            }) {
                it[discount] = manipulateOrderItem.discount
                it[modifiedAt] = LocalDateTime.now()
            }

            return@transaction getOrderItemsByUUID(orderUUID)
        }

    override fun removeOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): List<OrderItem>? =
        transaction {
            OrderItemDatabase.deleteWhere {
                productUUID eq manipulateOrderItem.productUUID and
                        (OrderItemDatabase.orderUUID eq orderUUID)
            }

            return@transaction getOrderItemsByUUID(orderUUID)
        }

    override fun getOrderItemByProductUUID(orderUUID: UUID, productUUID: UUID): OrderItem? =
        transaction {
            OrderItemDatabase
                .join(
                    otherTable = ProductDatabase,
                    joinType = JoinType.INNER,
                    additionalConstraint = { ProductDatabase.uuid eq OrderItemDatabase.productUUID }
                )
                .select(
                    ProductDatabase.uuid,
                    ProductDatabase.code,
                    ProductDatabase.name,
                    ProductDatabase.description,
                    ProductDatabase.price,
                    ProductDatabase.weight,
                    OrderItemDatabase.quantity,
                    OrderItemDatabase.discount,
                    OrderItemDatabase.createdAt,
                    OrderItemDatabase.modifiedAt
                ).where {
                    OrderItemDatabase.productUUID eq productUUID
                }.map {
                    OrderItem(
                        quantity = it[OrderItemDatabase.quantity],
                        discount = it[OrderItemDatabase.discount],
                        product = Product(
                            uuid = it[ProductDatabase.uuid],
                            name = it[ProductDatabase.name],
                            code = it[ProductDatabase.code],
                            description = it[ProductDatabase.description],
                            price = it[ProductDatabase.price],
                            weight = it[ProductDatabase.weight],
                            status = enumFromTypeValue<ProductStatus, Int>(it[ProductDatabase.status]),
                        ),
                        createdAt = it[OrderItemDatabase.createdAt],
                        modifiedAt = it[OrderItemDatabase.modifiedAt],
                    )
                }.firstOrNull()
        }

    override fun getOrderItemsByUUID(orderUUID: UUID): List<OrderItem>? =
        transaction {
            OrderItemDatabase
                .join(
                    otherTable = ProductDatabase,
                    joinType = JoinType.INNER,
                    additionalConstraint = { ProductDatabase.uuid eq OrderItemDatabase.productUUID }
                )
                .select(
                    ProductDatabase.uuid,
                    ProductDatabase.code,
                    ProductDatabase.name,
                    ProductDatabase.description,
                    ProductDatabase.price,
                    ProductDatabase.weight,
                    OrderItemDatabase.quantity,
                    OrderItemDatabase.discount,
                    OrderItemDatabase.createdAt,
                    OrderItemDatabase.modifiedAt
                ).where {
                    OrderItemDatabase.orderUUID eq orderUUID
                }.map {
                    OrderItem(
                        quantity = it[OrderItemDatabase.quantity],
                        discount = it[OrderItemDatabase.discount],
                        product = Product(
                            uuid = it[ProductDatabase.uuid],
                            name = it[ProductDatabase.name],
                            code = it[ProductDatabase.code],
                            description = it[ProductDatabase.description],
                            price = it[ProductDatabase.price],
                            weight = it[ProductDatabase.weight],
                            status = enumFromTypeValue<ProductStatus, Int>(it[ProductDatabase.status]),
                        ),
                        createdAt = it[OrderItemDatabase.createdAt],
                        modifiedAt = it[OrderItemDatabase.modifiedAt],
                    )
                }
        }

    override fun changeOrderStatus(orderUUID: UUID, manipulateOrder: ManipulateOrder): Order? =
        transaction {
            OrderDatabase.update({
                OrderDatabase.uuid eq orderUUID
            }) {
                it[status] = manipulateOrder.status.code
                it[modifiedAt] = LocalDateTime.now()
            }

            return@transaction getOrderByUUID(orderUUID)
        }

    override fun getOrderByUUID(orderUUID: UUID): Order? = transaction {
        val order = OrderDatabase
            .join(
                otherTable = CustomerDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { CustomerDatabase.uuid eq OrderDatabase.customerUUID }
            )
            .join(
                otherTable = UserDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = { UserDatabase.uuid eq OrderDatabase.operatorUUID }
            )
            .select(
                OrderDatabase.uuid,
                OrderDatabase.code,
                OrderDatabase.discount,
                OrderDatabase.defaultDiscount,
                OrderDatabase.status,
                OrderDatabase.createdAt,
                OrderDatabase.modifiedAt,
                CustomerDatabase.uuid,
                CustomerDatabase.companyName,
                CustomerDatabase.tradingName,
                UserDatabase.uuid,
                UserDatabase.name,
                UserDatabase.surname,
                UserDatabase.login
            )
            .where({ OrderDatabase.uuid eq orderUUID })
            .map {
                resultRowToOrder(it)
            }.firstOrNull()

        return@transaction order
    }

    override fun getPaginatedOrder(count: Int, page: Int, params: Map<String, Any?>): Pagination<Order>? =
        transaction {
            val query = OrderDatabase
                .join(
                    otherTable = CustomerDatabase,
                    joinType = JoinType.INNER,
                    additionalConstraint = { CustomerDatabase.uuid eq OrderDatabase.customerUUID }
                )
                .join(
                    otherTable = UserDatabase,
                    joinType = JoinType.INNER,
                    additionalConstraint = { UserDatabase.uuid eq OrderDatabase.operatorUUID }
                )
                .select(
                    OrderDatabase.uuid,
                    OrderDatabase.code,
                    OrderDatabase.discount,
                    OrderDatabase.defaultDiscount,
                    OrderDatabase.status,
                    OrderDatabase.createdAt,
                    OrderDatabase.modifiedAt,
                    CustomerDatabase.uuid,
                    CustomerDatabase.companyName,
                    CustomerDatabase.tradingName,
                    UserDatabase.uuid,
                    UserDatabase.name,
                    UserDatabase.surname,
                    UserDatabase.login
                )
                .where(OrderFilter(params).toFilter(OrderDatabase))

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    resultRowToOrder(it)
                }

            Pagination(
                items = items,
                page = page,
                total = total,
            )
        }

    private fun resultRowToOrder(it: ResultRow): Order = Order(
        uuid = it[OrderDatabase.uuid],
        code = it[OrderDatabase.code],
        discount = it[OrderDatabase.discount],
        defaultDiscount = it[OrderDatabase.defaultDiscount],
        customer = Customer(
            uuid = it[CustomerDatabase.uuid],
            companyName = it[CustomerDatabase.companyName],
            tradingName = it[CustomerDatabase.tradingName]
        ),
        operator = User(
            uuid = it[UserDatabase.uuid],
            name = it[UserDatabase.name],
            surname = it[UserDatabase.surname],
            login = it[UserDatabase.login],
        ),
        status = enumFromTypeValue<OrderStatus, Int>(it[OrderDatabase.status]),
        createdAt = it[OrderDatabase.createdAt],
        modifiedAt = it[OrderDatabase.modifiedAt],
    )
}