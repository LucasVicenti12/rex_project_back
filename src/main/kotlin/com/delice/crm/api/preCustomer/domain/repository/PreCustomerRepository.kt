package com.delice.crm.api.preCustomer.domain.repository

import com.delice.crm.api.preCustomer.domain.entities.PreCustomer

interface PreCustomerRepository {
    fun getPreCustomerInBase(document: String): PreCustomer?
    fun getPreCustomerInAPIBase(document: String): PreCustomer?
    fun savePreCustomer(preCustomer: PreCustomer)
}