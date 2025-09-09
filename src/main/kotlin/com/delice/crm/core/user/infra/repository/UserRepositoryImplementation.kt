package com.delice.crm.core.user.infra.repository

import com.delice.crm.core.user.domain.entities.SimpleUser
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType
import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.user.infra.database.UserFilter
import com.delice.crm.core.user.infra.database.UserOrderBy
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.core.utils.pagination.Pagination
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Service
import java.time.LocalDateTime
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

    override fun getUserPagination(page: Int, count: Int, orderBy: OrderBy?, params: Map<String, Any?>): Pagination<User>? = transaction {
        val query = UserDatabase
            .selectAll()
            .where(UserFilter(params).toFilter(UserDatabase))
            .orderBy(UserOrderBy(orderBy).toOrderBy())

        val total = ceil(query.count().toDouble() / count).toInt()

        val items = query
            .orderBy(UserDatabase.name)
            .limit(count)
            .offset((page * count).toLong())
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

        val user = getUserByUUID(userUUID)

        if (user != null && user.status === UserStatus.FIRST_ACCESS) {
            UserDatabase.update({
                UserDatabase.uuid eq userUUID
            }) {
                it[status] = UserStatus.ACTIVE.code
            }
        }

        return@transaction user
    }

    override fun changeUser(user: User): User? = transaction {
        UserDatabase.update({ UserDatabase.uuid eq user.uuid!! }) {
            it[userType] = user.userType!!.type
            it[email] = user.email!!
            it[status] = user.status!!.code
            it[phone] = user.phone
            it[state] = user.state!!
            it[city] = user.city!!
            it[zipCode] = user.zipCode!!
            it[address] = user.address!!
            it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction getUserByUUID(user.uuid!!)
    }

    override fun listSimpleUsers(): List<SimpleUser>? = transaction {
        UserDatabase.select(UserDatabase.uuid, UserDatabase.login, UserDatabase.name, UserDatabase.surname)
            .where(UserDatabase.status neq UserStatus.INACTIVE.code)
            .map {
                SimpleUser(
                    uuid = it[UserDatabase.uuid],
                    login = it[UserDatabase.login],
                    userName = "${it[UserDatabase.name]} ${it[UserDatabase.surname]}",
                )
            }
    }

    override fun changeUserAvatar(userUuid: UUID, imageBase64: String) {
        transaction {
            UserDatabase.update ({UserDatabase.uuid eq userUuid}){
                it[avatar] = ExposedBlob(imageBase64.toByteArray())
            }
        }
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
        address = it[UserDatabase.address],
        createdAt = it[UserDatabase.createdAt],
        modifiedAt = it[UserDatabase.modifiedAt],
    )
}