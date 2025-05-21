package com.delice.crm.core.utils.contact

import java.util.UUID

class Contact(
    var uuid: UUID? = null,
    var contactType: ContactType? = ContactType.NONE,
    var label: String? = null,
    var isPrincipal: Boolean = false,
)