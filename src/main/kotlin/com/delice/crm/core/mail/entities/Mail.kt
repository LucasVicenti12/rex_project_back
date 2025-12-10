package com.delice.crm.core.mail.entities

data class Mail(
    val subject: String,
    val content: String,
    val to: String,
    val withHtml: Boolean? = false
)

data class ResendMailRequest(
    val from: String,
    val to: String,
    val subject: String,
    val html: String,
)