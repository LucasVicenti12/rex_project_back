package com.delice.crm.core.mail.queue

import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.service.MailService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.LinkedList

@Component
class MailQueue(
    private val service: MailService,
    private val list: LinkedList<Mail>
) : Thread() {
    companion object {
        private val logger = LoggerFactory.getLogger(MailQueue::class.java)
    }

    init {
        start()
    }

    fun addMail(mail: Mail) {
        list.add(mail)
    }

    override fun run() {
        while (true) {
            try {
                var mail: Mail?

                synchronized(list) {
                    mail = list.poll() ?: null
                }

                if (mail != null) {
                    service.public(mail!!)
                }
            } catch (e: Exception) {
                logger.error("ERROR ON MAIL QUEUE -> ${e.message}", e)
            }
        }
    }
}