package com.delice.crm.core.user.domain.usecase.implementation

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.exceptions.*
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.user.domain.usecase.UserUseCase
import com.delice.crm.core.user.domain.usecase.response.SimpleUsersResponse
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
        } else {
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

    override fun getUserPagination(page: Int, count: Int, params: Map<String, Any?>): UserPaginationResponse = try {
        val pagination = userRepository.getUserPagination(page, count, params)

        UserPaginationResponse(
            users = pagination,
            error = null
        )
    } catch (e: Exception) {
        logger.error("USER_MODULE_PAGINATION", e)

        UserPaginationResponse(
            users = null,
            error = USER_UNEXPECTED
        )
    }

    override fun changeUser(user: User): UserResponse = try {
        when {
            user.name.isNullOrBlank() || user.surname.isNullOrBlank() -> {
                UserResponse(
                    user = null,
                    error = NAME_MUST_BE_PROVIDED
                )
            }

            user.email.isNullOrBlank() -> {
                UserResponse(
                    user = null,
                    error = EMAIL_MUST_BE_PROVIDED
                )
            }

            user.state.isNullOrBlank() -> {
                UserResponse(
                    user = null,
                    error = STATE_MUST_BE_PROVIDED
                )
            }

            user.city.isNullOrBlank() -> {
                UserResponse(
                    user = null,
                    error = CITY_MUST_BE_PROVIDED
                )
            }

            user.zipCode.isNullOrBlank() -> {
                UserResponse(
                    user = null,
                    error = ZIP_CODE_MUST_BE_PROVIDED
                )
            }

            user.address.isNullOrBlank() -> {
                UserResponse(
                    user = null,
                    error = ADDRESS_MUST_BE_PROVIDER
                )
            }

            else -> {
                UserResponse(user = userRepository.changeUser(user), error = null)
            }
        }
    } catch (e: Exception) {
        logger.error("USER_MODULE_GET_BY_UUID", e)

        UserResponse(
            user = null,
            error = USER_UNEXPECTED
        )
    }

    override fun listSimpleUsers(): SimpleUsersResponse = try {
        SimpleUsersResponse(
            users = userRepository.listSimpleUsers()
        )
    } catch (e: Exception) {
        logger.error("LIST_SIMPLE_USERS", e)

        SimpleUsersResponse(
            users = null,
            error = USER_UNEXPECTED
        )
    }
}