package com.delice.crm.core.user.domain.usecase.response

import com.delice.crm.core.user.domain.entities.SimpleUser
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.exceptions.UserException
import com.delice.crm.core.utils.pagination.Pagination

data class UserResponse(
    val user: User? = null,
    val error: UserException? = null,
)

data class UserPaginationResponse(
    val users: Pagination<User>? = null,
    val error: UserException? = null,
)

data class SimpleUsersResponse(
    val users: List<SimpleUser>? = emptyList(),
    val error: UserException? = null,
)

data class ChangeAvatarResponse(
    val ok: Boolean? = null,
    val error: UserException? = null
)