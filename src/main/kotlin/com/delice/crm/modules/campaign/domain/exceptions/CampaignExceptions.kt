package com.delice.crm.modules.campaign.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val CAMPAIGN_UNEXPECTED_ERROR = CampaignExceptions("CAMPAIGN_UNEXPECTED_ERROR", "An unexpected error has occurred")
val CAMPAIGN_NOT_FOUND = CampaignExceptions("CAMPAIGN_NOT_FOUND", "The campaign does not exist")
val CAMPAIGN_LABEL_IS_EMPTY = CampaignExceptions("CAMPAIGN_LABEL_IS_EMPTY", "The campaign label must be provided")

val CAMPAIGN_MEDIA_MUST_BE_PRINCIPAL = CampaignExceptions("CAMPAIGN_MEDIA_MUST_BE_PRINCIPAL", "Only one product media can be principal")
val CAMPAIGN_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL = CampaignExceptions("CAMPAIGN_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL", "At least one product media must be principal")

class CampaignExceptions(code: String, message: String): DefaultError(code = code, message = message)