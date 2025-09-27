package com.delice.crm.modules.campaign.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val CAMPAIGN_UNEXPECTED_ERROR = CampaignExceptions("CAMPAIGN_UNEXPECTED_ERROR", "An unexpected error has occurred")
val CAMPAIGN_NOT_FOUND = CampaignExceptions("CAMPAIGN_NOT_FOUND", "The wallet does not exist")
val CAMPAIGN_LABEL_IS_EMPTY = CampaignExceptions("CAMPAIGN_LABEL_IS_EMPTY", "The wallet label must be provided")

val CAMPAIGN_MEDIA_MUST_BE_PRINCIPAL = CampaignExceptions("CAMPAIGN_MEDIA_MUST_BE_PRINCIPAL", "Only one product media can be principal")
val CAMPAIGN_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL = CampaignExceptions("CAMPAIGN_MEDIA_AT_LEAST_MUST_BE_PRINCIPAL", "At least one product media must be principal")

val CAMPAIGN_PRODUCT_ALREADY_ATTACHED = CampaignExceptions("CAMPAIGN_PRODUCT_ALREADY_ATTACHED", "This customer already attached in another wallet")
val CAMPAIGN_PRODUCT_DUPLICATE = CampaignExceptions("CAMPAIGN_PRODUCT_DUPLICATE", "The customer was selected more than once")
val CAMPAIGN_USER_NOT_FOUND = CampaignExceptions("CAMPAIGN_USER_NOT_FOUND", "This user does not exist")
val CAMPAIGN_PRODUCT_NOT_FOUND = CampaignExceptions("CAMPAIGN_PRODUCT_NOT_FOUND", "Customer not found")
val CAMPAIGN_PRODUCT_IS_EMPTY = CampaignExceptions("CAMPAIGN_PRODUCT_IS_EMPTY", "At least one customer must be provided")

class CampaignExceptions(code: String, message: String): DefaultError(code = code, message = message)