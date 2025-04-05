package com.delice.crm.core.mail.service

import com.delice.crm.core.mail.entities.Mail
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mail: JavaMailSender
) {
    companion object {
        private val logger = LoggerFactory.getLogger(MailService::class.java.name)
    }

    @Value("\${spring.mail.from}")
    private val from: String? = null

    fun public(message: Mail): Boolean = try {
        val mime = mail.createMimeMessage()
        val helper = MimeMessageHelper(mime, true)

        helper.setFrom(from!!)
        helper.setTo(message.to)
        helper.setSubject(message.subject)
        helper.setText(message.content)

        mail.send(helper.mimeMessage)

        true
    } catch (e: Exception) {
        logger.error("Could not send simple mail: ${e.message}", e)
        false
    }
}