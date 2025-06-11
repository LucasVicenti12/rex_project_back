package com.delice.crm.core.user.infra.web

import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.config.service.TokenService
import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.usecase.UserUseCase
import com.delice.crm.core.user.domain.usecase.response.ChangeAvatarResponse
import com.delice.crm.core.user.domain.usecase.response.SimpleUsersResponse
import com.delice.crm.core.user.domain.usecase.response.UserPaginationResponse
import com.delice.crm.core.user.domain.usecase.response.UserResponse
import com.delice.crm.core.utils.filter.parametersToMap
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserWebService(
    private val userUseCase: UserUseCase,
    private val authUseCase: AuthUseCase
) {
    @Autowired
    private lateinit var tokenService: TokenService

    @GetMapping("/getByUUID")
    fun getUserByUUID(
        @RequestParam(
            value = "uuid",
            required = true
        ) uuid: UUID
    ): ResponseEntity<UserResponse> {
        val response = userUseCase.getUserByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getPagination")
    fun getUserByUUID(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        request: HttpServletRequest
    ): ResponseEntity<UserPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = userUseCase.getUserPagination(page, count, params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/update")
    fun getUserByUUID(
        @RequestBody user: User
    ): ResponseEntity<UserResponse> {
        val response = userUseCase.changeUser(user)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/simple")
    fun listSimpleUsers(): ResponseEntity<SimpleUsersResponse> {
        val response = userUseCase.listSimpleUsers()

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
    @PutMapping("/changeAvatar")
    fun changeAvatar(
        @RequestBody imageBase64 : String,
        request: HttpServletRequest
    ): ResponseEntity<ChangeAvatarResponse> {
        val token = tokenService.recoverToken(request)
        val login = tokenService.validate(token)
        val user = authUseCase.findUserByLogin(login)

        if (user === null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }
        val response = userUseCase.changeUserAvatar(user.uuid!!, imageBase64)
        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
}