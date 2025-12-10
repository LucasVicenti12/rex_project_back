package com.delice.crm.core.mail.service

import com.delice.crm.core.mail.entities.Mail

interface MailService {
    fun public(message: Mail): Boolean
}