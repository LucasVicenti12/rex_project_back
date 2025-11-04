package com.delice.crm.modules.campaign.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val CAMPAIGN_UNEXPECTED_ERROR = CampaignException("CAMPAIGN_UNEXPECTED_ERROR", "An unexpected error has occurred")
val CAMPAIGN_NOT_FOUND = CampaignException("CAMPAIGN_NOT_FOUND", "The campaign does not exist")
val CAMPAIGN_TITLE_IS_EMPTY = CampaignException("CAMPAIGN_TITLE_IS_EMPTY", "The title is empty")
val CAMPAIGN_OBJECTIVE_IS_EMPTY = CampaignException("CAMPAIGN_OBJECTIVE_IS_EMPTY", "The title is empty")

class CampaignException(code: String, message: String): DefaultError(code = code, message = message)