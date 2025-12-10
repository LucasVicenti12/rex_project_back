package com.delice.crm.core.mail.service.implementation

import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.entities.ResendMailRequest
import com.delice.crm.core.mail.service.MailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


@Service
class ResendMailServiceImplementation : MailService {
    companion object {
        private val logger = LoggerFactory.getLogger(ResendMailServiceImplementation::class.java.name)
    }

    @Value("\${resend.api.from}")
    private val from: String? = null

    @Value("\${resend.api.url}")
    private val url: String? = null

    @Value("\${resend.api.key}")
    private val apiKey: String? = null

    override fun public(message: Mail): Boolean = try {
        val requestObj = ResendMailRequest(
            from = from!!,
            to = message.to,
            subject = message.subject,
            html = message.content,
        )

        WebClient.create()
            .post()
            .uri(url!!)
            .bodyValue(requestObj)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        true
    } catch (e: Exception) {
        logger.error("Could not send simple mail: ${e.message}", e)
        false
    }
}