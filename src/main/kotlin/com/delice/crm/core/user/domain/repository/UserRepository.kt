package com.delice.crm.core.user.domain.repository

import com.delice.crm.core.user.domain.entities.SimpleUser
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.UUID

@Service
interface UserRepository {
    fun getUserByDocument(document: String): User?
    fun getUserByUUID(uuid: UUID): User?
    fun getUserPagination(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): Pagination<User>?
    fun getUserByEmail(email: String): User?
    fun changePassword(userUUID: UUID, newPassword: String): User?
    fun changeUser(user: User): User?
    fun listSimpleUsers(): List<SimpleUser>?
    fun changeUserAvatar(userUuid: UUID, imageBase64: String)
}