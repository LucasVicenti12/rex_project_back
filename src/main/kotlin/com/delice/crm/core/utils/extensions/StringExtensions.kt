package com.delice.crm.core.utils.extensions

fun String.removeSpecialChars(): String = this.replace(Regex("[^a-zA-Z0-9]"), "")