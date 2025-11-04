package com.delice.crm.modules.campaign.domain.entities

import com.delice.crm.core.utils.enums.HasCode
import com.delice.crm.core.utils.enums.HasType

enum class CampaignStatus(override val code: Int): HasCode {
    ACTIVE(0),
    INACTIVE(1),
    FORM_PENDING(2)
}

enum class CampaignType(override val code: Int): HasCode {
    SALE(0),
    LEAD(1)
}

enum class CampaignLeadFieldType(override val type: String): HasType {
    DOCUMENT("document"),
    COMPANY_NAME("company_name"),
    TRADING_NAME("trading_name"),
    EMAIL("email"),
    ECONOMIC_ACTIVITY("economic_activity"),
    PHONE_NUMBER("phone_number"),
    CEP("cep"),
    CITY("city"),
    STATE("state"),
    COMPLEMENT("complement"),
    ADDRESS("address"),
    ADDRESS_NUMBER("address_number"),
    PERSONAL_NAME("personal_name"),
}