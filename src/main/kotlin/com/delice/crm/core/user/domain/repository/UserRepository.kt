package com.delice.crm.core.user.domain.repository

import com.delice.crm.core.auth.domain.usecase.response.ChangePasswordResponse
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.utils.pagination.Pagination
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface UserRepository {
    fun getUserByDocument(document: String): User?
    fun getUserByUUID(uuid: UUID): User?
    fun getUserPagination(page: Int, count: Int): Pagination<User>?
    fun getUserByEmail(email: String): User?
    fun changePassword(userUUID: UUID, newPassword: String): User?
}