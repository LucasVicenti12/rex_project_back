package com.delice.crm.core.auth.infra.repository

import com.delice.crm.core.auth.domain.repository.AuthRepository
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class AuthRepositoryImplementation : AuthRepository {
    override fun findUserByLogin(login: String): User? = transaction {
        UserDatabase
            .selectAll()
            .where(UserDatabase.login eq login).map {
            User(
                uuid = it[UserDatabase.uuid],
                login = it[UserDatabase.login],
                pass = it[UserDatabase.password],
                userType = enumFromTypeValue<UserType, String>(it[UserDatabase.userType]),
            )
        }.firstOrNull()
    }

    override fun registerUser(user: User): User? = transaction {
        UserDatabase.insert {
            it[uuid] = user.uuid!!
            it[login] = user.login!!
            it[password] = user.pass!!
            it[userType] = user.userType!!.type
            it[email] = user.email!!
            if (user.avatar != null) {
                it[avatar] = ExposedBlob(user.avatar.toByteArray())
            }
            it[status] = user.status!!.code
            it[name] = user.name!!
            it[surname] = user.surname!!
        }.resultedValues?.map {
            User(
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
        }?.firstOrNull()
    }
}