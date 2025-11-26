package com.delice.crm.modules.dashboard.domain.entities

import com.delice.crm.modules.product.domain.entities.SimpleProduct
import com.delice.crm.modules.product.domain.entities.SimpleProductWithSales

class DashboardCustomerValues (
    var pending: Int,
    var inactive: Int,
    var fit: Int,
    var notFit: Int
)

class DashboardOrderValues (
    var open: Int,
    var closed: Int,
    var canceled: Int
)

class DashboardRankProductsValues (
    var Products: List<SimpleProductWithSales>
)

data class MonthlySales(
    val monthYear: String,
    val total: Double
)