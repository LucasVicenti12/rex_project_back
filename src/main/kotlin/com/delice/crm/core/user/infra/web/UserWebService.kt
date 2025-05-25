package com.delice.crm.core.user.infra.web

import com.delice.crm.core.user.domain.entities.User
import com.delice.crm.core.user.domain.usecase.UserUseCase
import com.delice.crm.core.user.domain.usecase.response.SimpleUsersResponse
import com.delice.crm.core.user.domain.usecase.response.UserPaginationResponse
import com.delice.crm.core.user.domain.usecase.response.UserResponse
import com.delice.crm.core.utils.filter.parametersToMap
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/user")
class UserWebService(
    private val userUseCase: UserUseCase
) {
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
}