package com.delice.crm.core.user.infra.repository

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.user.infra.database.UserFilter
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.pagination.Pagination
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.ceil

@Service
class UserRepositoryImplementation : UserRepository {
    override fun getUserByDocument(document: String): User? = transaction {
        UserDatabase.selectAll().where(UserDatabase.document eq document).map {
            convertResultRowToUser(it)
        }.firstOrNull()
    }

    override fun getUserByUUID(uuid: UUID): User? = transaction {
        UserDatabase.selectAll().where(UserDatabase.uuid eq uuid).map {
            convertResultRowToUser(it)
        }.firstOrNull()
    }

    override fun getUserPagination(page: Int, count: Int, params: Map<String, Any?>): Pagination<User>? = transaction {
        val query = UserDatabase
            .selectAll()
            .where(UserFilter(params).toFilter(UserDatabase))

        val total = ceil(query.count().toDouble() / count).toInt()
        val items = query
            .orderBy(UserDatabase.name)
            .limit(count)
            .offset(page.toLong())
            .map {
                convertResultRowToUser(it)
            }

        Pagination(
            items = items,
            page = page,
            total = total,
        )
    }

    override fun getUserByEmail(email: String): User? = transaction {
        UserDatabase.selectAll()
            .where { UserDatabase.email eq email }.map {
                convertResultRowToUser(it)
            }
            .firstOrNull()
    }

    override fun changePassword(userUUID: UUID, newPassword: String): User? = transaction {
        UserDatabase.update({
            UserDatabase.uuid eq userUUID
        }) {
            it[password] = newPassword
        }

        return@transaction getUserByUUID(userUUID)
    }

    private fun convertResultRowToUser(it: ResultRow): User = User(
        uuid = it[UserDatabase.uuid],
        login = it[UserDatabase.login],
        pass = null,
        userType = enumFromTypeValue<UserType, String>(it[UserDatabase.userType]),
        email = it[UserDatabase.email],
        avatar = binaryToString(it[UserDatabase.avatar]),
        status = enumFromTypeValue<UserStatus, Int>(it[UserDatabase.status]),
        name = it[UserDatabase.name],
        surname = it[UserDatabase.surname],
        document = it[UserDatabase.document],
        phone = it[UserDatabase.phone],
        dateOfBirth = it[UserDatabase.dateOfBirth],
        state = it[UserDatabase.state],
        city = it[UserDatabase.city],
        zipCode = it[UserDatabase.zipCode],
        createdAt = it[UserDatabase.createdAt],
        modifiedAt = it[UserDatabase.modifiedAt],
    )
}