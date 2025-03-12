package com.delice.crm.core.auth.domain.usecase.implementation

import com.delice.crm.core.auth.domain.entities.Login
import com.delice.crm.core.auth.domain.entities.Register
import com.delice.crm.core.auth.domain.exceptions.*
import com.delice.crm.core.auth.domain.repository.AuthRepository
import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.auth.domain.usecase.response.LoginResponse
import com.delice.crm.core.auth.domain.usecase.response.RegisterResponse
import com.delice.crm.core.config.entities.SystemUser
import com.delice.crm.core.config.service.TokenService
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthUseCaseImplementation(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val authentication: AuthenticationManager
) : AuthUseCase {
    @Autowired
    private lateinit var tokenService: TokenService

    companion object{
        private var logger = LoggerFactory.getLogger(AuthUseCaseImplementation::class.java)
    }

    override fun registerUser(register: Register): RegisterResponse {
        try {
            if (register.login.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = LOGIN_MUST_BE_PROVIDED
                )
            }

            if (register.password.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = PASSWORD_MUST_BE_PROVIDED
                )
            }

            if (register.document.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = DOCUMENT_MUST_BE_PROVIDED
                )
            }

            if (register.name.isNullOrBlank() || register.surname.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = NAME_MUST_BE_PROVIDED
                )
            }

            if (userRepository.getUserByDocument(register.document) != null) {
                return RegisterResponse(
                    user = null,
                    error = DOCUMENT_ALREADY_REGISTERED
                )
            }

            if (authRepository.findUserByLogin(register.login) != null) {
                return RegisterResponse(
                    user = null,
                    error = LOGIN_ALREADY_REGISTERED
                )
            }

            val encryptedPass = BCryptPasswordEncoder().encode(register.password)

            val user = User(
                uuid = UUID.randomUUID(),
                login = register.login,
                pass = encryptedPass,
                userType = register.userType,
                email = register.email,
                avatar = register.avatar,
                status = register.status,
                name = register.name,
                surname = register.surname
            )

            val userResponse = authRepository.registerUser(user)

            return RegisterResponse(
                user = userResponse,
                error = null
            )
        }catch (e:Exception){
            logger.error("AUTH_MODULE_REGISTER", e)

            return RegisterResponse(
                user = null,
                error = AUTH_UNEXPECTED
            )
        }
    }

    override fun loginUser(login: Login): LoginResponse {
        try {
            if (login.login.isNullOrBlank()) {
                return LoginResponse(
                    token = null,
                    error = LOGIN_MUST_BE_PROVIDED
                )
            }

            if (login.password.isNullOrBlank()) {
                return LoginResponse(
                    token = null,
                    error = PASSWORD_MUST_BE_PROVIDED
                )
            }

            val userName = UsernamePasswordAuthenticationToken(login.login, login.password)

            val auth = authentication.authenticate(userName)
            val user = auth.principal as SystemUser

            val token = tokenService.generate(user.getUserData())

            return LoginResponse(token = token, error = null)
        }catch (e:Exception){
            logger.error("AUTH_MODULE_LOGIN", e)

            return LoginResponse(
                token = null,
                error = AUTH_UNEXPECTED
            )
        }
    }

    override fun getGrantedAuthorities(user: User): List<GrantedAuthority> {
        TODO("Not yet implemented")
    }
}