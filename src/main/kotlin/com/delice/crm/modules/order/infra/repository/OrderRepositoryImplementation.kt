package com.delice.crm.modules.order.infra.repository

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.extensions.round
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.pagination.Pagination
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.order.domain.entities.*
import com.delice.crm.modules.order.domain.repository.OrderRepository
import com.delice.crm.modules.order.infra.database.OrderDatabase
import com.delice.crm.modules.order.infra.database.OrderFilter
import com.delice.crm.modules.order.infra.database.OrderItemDatabase
import com.delice.crm.modules.product.domain.entities.Product
import com.delice.crm.modules.product.domain.entities.ProductMedia
import com.delice.crm.modules.product.domain.entities.ProductStatus
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.product.infra.database.ProductMediaDatabase
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
            it[defaultDiscount] = order.defaultDiscount ?: 0.0
            it[customerUUID] = order.customer!!.uuid!!
            it[status] = order.status!!.code
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
            it[operatorUUID] = order.operator!!.uuid!!
        }

        return@transaction getOrderByUUID(newOrderUUID)
    }

    override fun saveOrder(
        orderUUID: UUID,
        manipulateOrder: ManipulateOrder
    ): Order? = transaction {
        OrderDatabase.update({ OrderDatabase.uuid eq orderUUID }) {
            it[defaultDiscount] = manipulateOrder.discount
            it[status] = manipulateOrder.status.code
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getOrderByUUID(orderUUID)
    }

    override fun saveOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): Order? =
        transaction {
            manipulateOrderItem.products.forEach { product ->
                val item = getOrderItemByProductUUID(orderUUID, product)

                if (item == null) {
                    OrderItemDatabase.insert {
                        it[OrderItemDatabase.orderUUID] = orderUUID
                        it[quantity] = manipulateOrderItem.quantity
                        it[discount] = manipulateOrderItem.discount
                        it[productUUID] = product
                        it[createdAt] = LocalDateTime.now()
                        it[modifiedAt] = LocalDateTime.now()
                    }
                } else {
                    OrderItemDatabase.update({
                        OrderItemDatabase.productUUID eq product and
                                (OrderItemDatabase.orderUUID eq orderUUID)
                    }) {
                        it[quantity] = manipulateOrderItem.quantity
                        it[discount] = manipulateOrderItem.discount
                        it[modifiedAt] = LocalDateTime.now()
                    }
                }
            }

            return@transaction getOrderByUUID(orderUUID)
        }

    override fun removeOrderItem(orderUUID: UUID, manipulateOrderItem: ManipulateOrderItem): Order? =
        transaction {
            manipulateOrderItem.products.forEach { product ->
                OrderItemDatabase.deleteWhere {
                    productUUID eq product and
                            (OrderItemDatabase.orderUUID eq orderUUID)
                }
            }

            return@transaction getOrderByUUID(orderUUID)
        }

    override fun getOrderItemByProductUUID(orderUUID: UUID, productUUID: UUID): OrderItem? =
        transaction {
            OrderItemDatabase
                .select(
                    OrderItemDatabase.quantity,
                    OrderItemDatabase.discount,
                ).where {
                    OrderItemDatabase.productUUID eq productUUID and (
                            OrderItemDatabase.orderUUID eq orderUUID
                            )
                }.map {
                    OrderItem(
                        quantity = it[OrderItemDatabase.quantity],
                        discount = it[OrderItemDatabase.discount],
                    )
                }.firstOrNull()
        }

    override fun getOrderItemsByUUID(orderUUID: UUID): List<OrderItem>? =
        transaction {
            OrderItemDatabase
                .join(
                    otherTable = ProductDatabase,
                    joinType = JoinType.INNER,
                    additionalConstraint = {
                        ProductDatabase.uuid eq OrderItemDatabase.productUUID
                    }
                )
                .join(
                    otherTable = ProductMediaDatabase,
                    joinType = JoinType.LEFT,
                    additionalConstraint = {
                        OrderItemDatabase.productUUID eq ProductMediaDatabase.productUUID and (
                                ProductMediaDatabase.isPrincipal eq Op.TRUE
                                )
                    },
                )
                .select(
                    ProductDatabase.uuid,
                    ProductDatabase.code,
                    ProductDatabase.name,
                    ProductDatabase.description,
                    ProductDatabase.price,
                    ProductDatabase.weight,
                    ProductDatabase.status,
                    ProductMediaDatabase.image,
                    ProductMediaDatabase.isPrincipal,
                    OrderItemDatabase.quantity,
                    OrderItemDatabase.discount,
                    OrderItemDatabase.createdAt,
                    OrderItemDatabase.modifiedAt
                ).where {
                    OrderItemDatabase.orderUUID eq orderUUID
                }.map {
                    val item = resultRowToOrderItem(it)

                    val totals = calculateOrderItemTotals(item)

                    item.grossPrice = totals.grossPrice
                    item.netPrice = totals.netPrice
                    item.weight = totals.weight

                    item
                }
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
                OrderDatabase.defaultDiscount,
                OrderDatabase.status,
                OrderDatabase.createdAt,
                OrderDatabase.modifiedAt,
                CustomerDatabase.uuid,
                CustomerDatabase.companyName,
                CustomerDatabase.tradingName,
                CustomerDatabase.personName,
                CustomerDatabase.document,
                CustomerDatabase.state,
                CustomerDatabase.city,
                CustomerDatabase.zipCode,
                CustomerDatabase.address,
                CustomerDatabase.complement,
                CustomerDatabase.addressNumber,
                CustomerDatabase.observation,
                CustomerDatabase.status,
                UserDatabase.uuid,
                UserDatabase.name,
                UserDatabase.surname,
                UserDatabase.login,
                UserDatabase.avatar,
            )
            .where({ OrderDatabase.uuid eq orderUUID })
            .map {
                val order = resultRowToOrder(it)

                val items = getOrderItemsByUUID(order.uuid!!)

                if (!items.isNullOrEmpty()) {
                    order.items = items

                    val totals = calculateOrderTotals(order.uuid)

                    order.grossPrice = totals.grossPrice
                    order.netPrice = totals.netPrice
                    order.discount = totals.discount
                    order.totalItems = totals.totalItems
                    order.totalProducts = totals.totalProducts
                    order.weight = totals.weight
                }

                order
            }.firstOrNull()

        return@transaction order
    }

    fun calculateOrderTotals(orderUUID: UUID): OrderTotals = transaction {
        val items = OrderItemDatabase
            .join(
                otherTable = ProductDatabase,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    ProductDatabase.uuid eq OrderItemDatabase.productUUID
                }
            )
            .select(
                ProductDatabase.price,
                ProductDatabase.weight,
                OrderItemDatabase.quantity,
                OrderItemDatabase.discount,
            ).where {
                OrderItemDatabase.orderUUID eq orderUUID
            }.map {
                val item = OrderItem(
                    product = Product(
                        price = it[ProductDatabase.price],
                        weight = it[ProductDatabase.weight],
                    ),
                    quantity = it[OrderItemDatabase.quantity],
                    discount = it[OrderItemDatabase.discount],
                )

                val totals = calculateOrderItemTotals(item)

                item.grossPrice = totals.grossPrice
                item.netPrice = totals.netPrice
                item.weight = totals.weight

                item
            }

        val defaultDiscount = OrderDatabase.select(OrderDatabase.defaultDiscount).where {
            OrderDatabase.uuid eq orderUUID
        }.map {
            it[OrderDatabase.defaultDiscount]
        }.first()

        if (items.isNotEmpty()){
            var totalNet = 0.0
            var totalGross = 0.0
            var totalWeight = 0.0
            var totalItems = 0

            items.forEach { item ->
                totalNet += item.netPrice
                totalGross += item.grossPrice
                totalWeight += item.weight
                totalItems += item.quantity
            }

            totalNet -= (totalNet * (defaultDiscount / 100))

            val discountValue = totalGross - totalNet

            val discount = ((discountValue / totalGross) * 100).round(2)

            OrderTotals(
                grossPrice = totalGross.round(2),
                netPrice = totalNet.round(2),
                discount = discount,
                totalItems = totalItems,
                weight = totalWeight.round(2),
                totalProducts = items.size
            )
        }else{
            OrderTotals(
                grossPrice = 0.0,
                netPrice = 0.0,
                discount = 0.0,
                totalItems = 0,
                weight = 0.0,
                totalProducts = 0
            )
        }
    }

    fun calculateOrderItemTotals(item: OrderItem): OrderItemTotals {
        val grossPrice = (item.product!!.price!! * item.quantity).round(2)
        val netPrice = (grossPrice - (grossPrice * (item.discount / 100))).round(2)

        val weight = (item.product.weight!! * item.quantity).round(2)

        return OrderItemTotals(
            grossPrice = grossPrice,
            netPrice = netPrice,
            weight = weight
        )
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
                    OrderDatabase.defaultDiscount,
                    OrderDatabase.status,
                    OrderDatabase.createdAt,
                    OrderDatabase.modifiedAt,
                    CustomerDatabase.uuid,
                    CustomerDatabase.companyName,
                    CustomerDatabase.tradingName,
                    CustomerDatabase.personName,
                    CustomerDatabase.document,
                    CustomerDatabase.state,
                    CustomerDatabase.city,
                    CustomerDatabase.zipCode,
                    CustomerDatabase.address,
                    CustomerDatabase.complement,
                    CustomerDatabase.addressNumber,
                    CustomerDatabase.observation,
                    CustomerDatabase.status,
                    UserDatabase.uuid,
                    UserDatabase.name,
                    UserDatabase.surname,
                    UserDatabase.login,
                    UserDatabase.avatar,
                )
                .where(OrderFilter(params).toFilter(OrderDatabase))

            val total = ceil(query.count().toDouble() / count).toInt()

            val items = query
                .limit(count)
                .offset((page * count).toLong())
                .map {
                    val order = resultRowToOrder(it)

                    val totals = calculateOrderTotals(order.uuid!!)

                    order.grossPrice = totals.grossPrice
                    order.netPrice = totals.netPrice
                    order.discount = totals.discount
                    order.totalItems = totals.totalItems
                    order.totalProducts = totals.totalProducts
                    order.weight = totals.weight

                    order
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
        defaultDiscount = it[OrderDatabase.defaultDiscount],
        customer = Customer(
            uuid = it[CustomerDatabase.uuid],
            companyName = it[CustomerDatabase.companyName],
            tradingName = it[CustomerDatabase.tradingName],
            personName = it[CustomerDatabase.personName],
            document = it[CustomerDatabase.document],
            state = it[CustomerDatabase.state],
            city = it[CustomerDatabase.city],
            zipCode = it[CustomerDatabase.zipCode],
            address = it[CustomerDatabase.address],
            complement = it[CustomerDatabase.complement],
            addressNumber = it[CustomerDatabase.addressNumber],
            observation = it[CustomerDatabase.observation],
            status = enumFromTypeValue<CustomerStatus, Int>(it[CustomerDatabase.status]),
        ),
        operator = User(
            uuid = it[UserDatabase.uuid],
            name = it[UserDatabase.name],
            surname = it[UserDatabase.surname],
            login = it[UserDatabase.login],
            avatar = binaryToString(it[UserDatabase.avatar]),
        ),
        status = enumFromTypeValue<OrderStatus, Int>(it[OrderDatabase.status]),
        createdAt = it[OrderDatabase.createdAt],
        modifiedAt = it[OrderDatabase.modifiedAt],
    )

    private fun resultRowToOrderItem(it: ResultRow): OrderItem = OrderItem(
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
            images = listOf(
                ProductMedia(
                    image = binaryToString(it[ProductMediaDatabase.image]),
                    isPrincipal = it[ProductMediaDatabase.isPrincipal]
                )
            )
        ),
        createdAt = it[OrderItemDatabase.createdAt],
        modifiedAt = it[OrderItemDatabase.modifiedAt],
    )
}