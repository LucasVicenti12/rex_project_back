package com.delice.crm.core.user.domain.usecase

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.usecase.response.ChangeAvatarResponse
import com.delice.crm.core.user.domain.usecase.response.SimpleUsersResponse
import com.delice.crm.core.user.domain.usecase.response.UserPaginationResponse
import com.delice.crm.core.user.domain.usecase.response.UserResponse
import java.util.UUID

interface UserUseCase {
    fun getUserByUUID(uuid: UUID): UserResponse
    fun getUserPagination(page: Int, count: Int, params: Map<String, Any?>): UserPaginationResponse
    fun changeUser(user: User): UserResponse
    fun listSimpleUsers(): SimpleUsersResponse
    fun changeUserAvatar(userUUID: UUID, imageBase64: String): ChangeAvatarResponse
}