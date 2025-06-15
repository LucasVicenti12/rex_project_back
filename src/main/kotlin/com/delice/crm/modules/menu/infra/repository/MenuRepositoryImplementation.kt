package com.delice.crm.modules.menu.infra.repository

import com.delice.crm.core.config.entities.SystemUser
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.modules.customer.infra.database.CustomerDatabase
import com.delice.crm.modules.menu.domain.entities.Menu
import com.delice.crm.modules.menu.domain.entities.MenuOption
import com.delice.crm.modules.menu.domain.entities.MenuOptionType
import com.delice.crm.modules.menu.domain.entities.MenuOptionValue
import com.delice.crm.modules.menu.domain.repository.MenuRepository
import com.delice.crm.modules.product.infra.database.ProductDatabase
import com.delice.crm.modules.wallet.infra.database.WalletDatabase
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class MenuRepositoryImplementation : MenuRepository {
    override fun queryMenuOptions(query: String, user: SystemUser): Menu? = transaction {
        val options = mutableListOf<MenuOption>()
        var totalResults = 0

        val querySafe = stringLiteral("%$query%")

        val roles = user.getRoles()

        if (roles.contains(element = "READ_CUSTOMER")) {
            val customers = CustomerDatabase.select(CustomerDatabase.uuid, CustomerDatabase.companyName).where {
                concat(
                    CustomerDatabase.companyName,
                    CustomerDatabase.personName,
                    CustomerDatabase.tradingName
                ) like querySafe
            }.map {
                MenuOptionValue(
                    uuid = it[CustomerDatabase.uuid],
                    value = it[CustomerDatabase.companyName],
                )
            }

            if(customers.isNotEmpty()){
                options.add(
                    MenuOption(
                        type = MenuOptionType.Customer.type,
                        values = customers
                    )
                )

                totalResults += customers.size
            }
        }

        if (roles.contains(element = "READ_USER")) {
            val users = UserDatabase.select(UserDatabase.uuid, UserDatabase.name, UserDatabase.surname).where {
                concat(UserDatabase.name, UserDatabase.surname) like querySafe
            }.map {
                MenuOptionValue(
                    uuid = it[UserDatabase.uuid],
                    value = "${it[UserDatabase.name]} ${it[UserDatabase.surname]}",
                )
            }

            if(users.isNotEmpty()){
                options.add(
                    MenuOption(
                        type = MenuOptionType.User.type,
                        values = users
                    )
                )

                totalResults += users.size
            }
        }

        if (roles.contains(element = "READ_WALLET")) {
            val wallets = WalletDatabase.select(WalletDatabase.uuid, WalletDatabase.label).where {
                WalletDatabase.label like querySafe
            }.map {
                MenuOptionValue(
                    uuid = it[WalletDatabase.uuid],
                    value = it[WalletDatabase.label],
                )
            }

            if(wallets.isNotEmpty()){
                options.add(
                    MenuOption(
                        type = MenuOptionType.Wallet.type,
                        values = wallets
                    )
                )

                totalResults += wallets.size
            }
        }

        if (roles.contains(element = "READ_PRODUCTS")) {
            val products = ProductDatabase.select(ProductDatabase.uuid, ProductDatabase.name).where {
                concat(ProductDatabase.code, ProductDatabase.name) like querySafe
            }.map {
                MenuOptionValue(
                    uuid = it[ProductDatabase.uuid],
                    value = it[ProductDatabase.name],
                )
            }

            if(products.isNotEmpty()){
                options.add(
                    MenuOption(
                        type = MenuOptionType.Product.type,
                        values = products
                    )
                )

                totalResults += products.size
            }
        }

        return@transaction Menu(
            query = query,
            totalResults = totalResults,
            result = options
        )
    }
}