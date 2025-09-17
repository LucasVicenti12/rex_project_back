package com.delice.crm.core.utils.extensions

import kotlin.math.pow

fun Double.round(digits: Int): Double {
    val fat = 10.0.pow(digits)

    return kotlin.math.round(this * fat) / fat
}