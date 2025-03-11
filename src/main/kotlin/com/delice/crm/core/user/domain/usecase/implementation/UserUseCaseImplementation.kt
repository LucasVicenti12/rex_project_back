package com.delice.crm.core.user.domain.usecase.implementation

import com.delice.crm.core.user.domain.exceptions.USER_NOT_FOUND
import com.delice.crm.core.user.domain.exceptions.USER_UNEXPECTED
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.user.domain.usecase.UserUseCase
import com.delice.crm.core.user.domain.usecase.response.UserPaginationResponse
import com.delice.crm.core.user.domain.usecase.response.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserUseCaseImplementation(
    private val userRepository: UserRepository,
) : UserUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(UserUseCaseImplementation::class.java)
    }

    override fun getUserByUUID(uuid: UUID): UserResponse = try {
        val user = userRepository.getUserByUUID(uuid)

        if (user != null) {
            UserResponse(
                user = user,
                error = null
            )
        }else{
            UserResponse(
                user = null,
                error = USER_NOT_FOUND
            )
        }
    } catch (e: Exception) {
        logger.error("USER_MODULE_GET_BY_UUID", e)

        UserResponse(
            user = null,
            error = USER_UNEXPECTED
        )
    }

    override fun getUserPagination(page: Int, count: Int): UserPaginationResponse = try {
        val pagination = userRepository.getUserPagination(page, count)

        UserPaginationResponse(
            users = pagination,
            error = null
        )
    }catch (e: Exception){
        logger.error("USER_MODULE_PAGINATION", e)

        UserPaginationResponse(
            users = null,
            error = USER_UNEXPECTED
        )
    }
}