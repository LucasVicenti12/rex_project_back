package com.delice.crm.core.mail.service.implementation

import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.service.MailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailServiceImplementation(
    private val mail: JavaMailSender
): MailService {
    companion object {
        private val logger = LoggerFactory.getLogger(MailServiceImplementation::class.java.name)
    }

    @Value("\${spring.mail.from}")
    private val from: String? = null

    override fun public(message: Mail): Boolean = try {
        val mime = mail.createMimeMessage()
        val helper = MimeMessageHelper(mime, true)

        val toList = message.to.split(";").toTypedArray()

        helper.setFrom(from!!)
        helper.setTo(toList)
        helper.setSubject(message.subject)
        helper.setText(message.content, message.withHtml!!)

        mail.send(helper.mimeMessage)

        true
    } catch (e: Exception) {
        logger.error("Could not send simple mail: ${e.message}", e)
        false
    }
}