package com.delice.crm.core.utils.function

import com.delice.crm.core.config.entities.SystemUser
import org.springframework.security.core.context.SecurityContextHolder

fun getCurrentUser(): SystemUser = SecurityContextHolder.getContext().authentication?.principal as SystemUser