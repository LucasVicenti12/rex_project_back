package com.delice.crm.core.user.infra.repository

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.pagination.Pagination
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.util.*

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

    override fun getUserPagination(page: Int, count: Int): Pagination<User>? = transaction {
        val total = UserDatabase.selectAll().count().toInt()
        val items = UserDatabase.selectAll().orderBy(UserDatabase.name).limit(count).offset(page.toLong()).map {
            convertResultRowToUser(it)
        }

        Pagination(
            items = items,
            page = page,
            total = total,
        )
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
        surname = it[UserDatabase.surname]
    )
}