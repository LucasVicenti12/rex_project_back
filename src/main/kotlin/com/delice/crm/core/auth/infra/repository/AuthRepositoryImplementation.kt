package com.delice.crm.core.auth.infra.repository

import com.delice.crm.core.auth.domain.repository.AuthRepository
import com.delice.crm.core.roles.domain.repository.RolesRepository
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.entities.UserType
import com.delice.crm.core.user.infra.database.UserDatabase
import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.function.binaryToString
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthRepositoryImplementation(
    private val rolesRepository: RolesRepository
) : AuthRepository {
    override fun findUserByLogin(login: String): User? = transaction {
        UserDatabase
            .selectAll()
            .where {
                (UserDatabase.login eq login) and (UserDatabase.status neq UserStatus.INACTIVE.code)
            }.map {
                User(
                    uuid = it[UserDatabase.uuid],
                    login = it[UserDatabase.login],
                    pass = it[UserDatabase.password],
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
            it[document] = user.document!!
            it[phone] = user.phone
            it[dateOfBirth] = user.dateOfBirth!!
            it[state] = user.state!!
            it[city] = user.city!!
            it[zipCode] = user.zipCode!!
            it[address] = user.address!!
            it[createdAt] = LocalDateTime.now()
            it[modifiedAt] = LocalDateTime.now()
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
        }?.firstOrNull()
    }

    override fun getGrantedAuthorities(user: User): List<GrantedAuthority> {
        if (user.status === UserStatus.FIRST_ACCESS) return emptyList()

        val roles = rolesRepository.getRolesPerUser(user.uuid!!)!!

        return roles.map { SimpleGrantedAuthority(it.code) }
    }
}