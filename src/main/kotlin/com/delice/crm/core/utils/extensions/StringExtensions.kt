package com.delice.crm.core.utils.extensions

fun String.removeSpecialChars(): String = this.replace(Regex("[^a-zA-Z0-9]"), "")
fun String.removeAlphaChars(): String = this.replace(Regex("[^0-9]"), "")