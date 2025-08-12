package com.delice.crm.core.mail.entities

data class Mail(
    val subject: String,
    val content: String,
    val to: String,
    val withHtml: Boolean? = false
)