package com.delice.crm.core.auth.domain.usecase.implementation

import com.delice.crm.core.auth.domain.entities.Login
import com.delice.crm.core.auth.domain.entities.Register
import com.delice.crm.core.auth.domain.entities.ResetPassword
import com.delice.crm.core.auth.domain.exceptions.*
import com.delice.crm.core.auth.domain.repository.AuthRepository
import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.auth.domain.usecase.response.*
import com.delice.crm.core.config.entities.SystemUser
import com.delice.crm.core.config.entities.TokenType
import com.delice.crm.core.config.service.TokenService
import com.delice.crm.core.mail.entities.Mail
import com.delice.crm.core.mail.queue.MailQueue
import com.delice.crm.core.roles.domain.repository.RolesRepository
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.entities.UserStatus
import com.delice.crm.core.user.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class AuthUseCaseImplementation(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val rolesRepository: RolesRepository,
    private val mailQueue: MailQueue
) : AuthUseCase {
    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var authentication: AuthenticationManager

    companion object {
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

            if (register.state.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = STATE_MUST_BE_PROVIDED
                )
            }

            if (register.city.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = CITY_MUST_BE_PROVIDED
                )
            }

            if (register.dateOfBirth === null) {
                return RegisterResponse(
                    user = null,
                    error = DATE_OF_BIRTH_MUST_BE_PROVIDED
                )
            }

            if (!register.dateOfBirth.isBefore(LocalDate.now().minusYears(14))) {
                return RegisterResponse(
                    user = null,
                    error = DATE_OF_BIRTH_INVALID
                )
            }

            if (register.zipCode.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = ZIP_CODE_MUST_BE_PROVIDED
                )
            }

            if (register.address.isNullOrBlank()) {
                return RegisterResponse(
                    user = null,
                    error = ADDRESS_MUST_BE_PROVIDED
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
                surname = register.surname,
                document = register.document,
                zipCode = register.zipCode,
                city = register.city,
                phone = register.phone,
                state = register.state,
                dateOfBirth = register.dateOfBirth,
            )

            val userResponse = authRepository.registerUser(user)

            return RegisterResponse(
                user = userResponse,
                error = null
            )
        } catch (e: Exception) {
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

            val token = tokenService.generate(user.getUserData(), TokenType.AUTH_REQUEST)

            return LoginResponse(token = token, error = null)
        } catch (e: Exception) {
            logger.error("AUTH_MODULE_LOGIN", e)

            return LoginResponse(
                token = null,
                error = AUTH_UNEXPECTED
            )
        }
    }

    override fun getGrantedAuthorities(user: User): List<GrantedAuthority> {
        try {
            return authRepository.getGrantedAuthorities(user)
        } catch (e: Exception) {
            logger.error("AUTH_MODULE_GET_AUTHORITY", e)
            return emptyList()
        }
    }

    override fun getAuthenticated(useUUID: UUID): AuthenticatedResponse {
        try {
            val user = userRepository.getUserByUUID(useUUID)
                ?: return AuthenticatedResponse(
                    error = AUTH_USER_NOT_FOUND
                )

            val modules =
                if (user.status === UserStatus.FIRST_ACCESS) emptyList()
                else rolesRepository.getModuleRolesByUserUUID(user.uuid!!)

            return AuthenticatedResponse(
                user = user,
                modules = modules
            )
        } catch (e: Exception) {
            logger.error("AUTH_MODULE_GET_AUTHENTICATED", e)
            return AuthenticatedResponse(
                error = AUTH_UNEXPECTED
            )
        }
    }

    override fun findUserByLogin(login: String): User? = authRepository.findUserByLogin(login)

    override fun forgotPassword(email: String, host: String): ForgotPasswordResponse {
        val user = userRepository.getUserByEmail(email) ?: return ForgotPasswordResponse(error = AUTH_USER_NOT_FOUND)

        val token = tokenService.generate(user, TokenType.RESET_REQUEST)

        mailQueue.addMail(
            Mail(
                subject = "Forgot Password request",
                to = user.email!!,
                content = "$host/app/resetPassword?token=$token",
            )
        )

        return ForgotPasswordResponse(error = null)
    }

    override fun resetPassword(userUUID: UUID, resetPassword: ResetPassword): ChangePasswordResponse {
        try {
            when {
                resetPassword.newPassword.isEmpty() -> {
                    return ChangePasswordResponse(error = PASSWORD_MUST_BE_PROVIDED)
                }

                resetPassword.confirmPassword.isEmpty() -> {
                    return ChangePasswordResponse(error = CONFIRM_PASSWORD_MUST_BE_PROVIDED)
                }

                resetPassword.newPassword != resetPassword.confirmPassword -> {
                    return ChangePasswordResponse(error = PASSWORDS_DONT_MATCH)
                }

                else -> {
                    val encryptedPassword = BCryptPasswordEncoder().encode(resetPassword.newPassword)

                    val user = userRepository.changePassword(userUUID, encryptedPassword)

                    return if (user != null) {
                        ChangePasswordResponse(ok = true)
                    } else {
                        ChangePasswordResponse(ok = false)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("AUTH_MODULE_RESET_PASSWORD", e)
            return ChangePasswordResponse(error = AUTH_UNEXPECTED)
        }
    }

    override fun resetPasswordWithToken(
        resetPassword: ResetPassword,
        token: String
    ): ChangePasswordResponse {
        try {
            if (token.isEmpty()) {
                return ChangePasswordResponse(error = TOKEN_MUST_BE_PROVIDED)
            }

            val login = tokenService.validate(token)
            val user = authRepository.findUserByLogin(login)

            return if (user != null) {
                resetPassword(user.uuid!!, resetPassword)
            } else {
                ChangePasswordResponse(error = INVALID_TOKEN)
            }
        } catch (e: Exception) {
            logger.error("AUTH_MODULE_RESET_PASSWORD_WITH_TOKEN", e)
            return ChangePasswordResponse(error = AUTH_UNEXPECTED)
        }
    }
}