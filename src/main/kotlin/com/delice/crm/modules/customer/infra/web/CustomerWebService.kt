package com.delice.crm.modules.customer.infra.web

import com.delice.crm.core.auth.domain.usecase.AuthUseCase
import com.delice.crm.core.config.service.TokenService
import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.usecase.CustomerUseCase
import com.delice.crm.modules.customer.domain.usecase.response.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/customer")
class CustomerWebService(
    private val customerUseCase: CustomerUseCase,
    private val authUseCase: AuthUseCase
) {

    @Autowired
    private lateinit var tokenService: TokenService

    @PostMapping("/register")
    fun registerCustomer(
        @RequestBody customer: Customer,
        request: HttpServletRequest
    ): ResponseEntity<CustomerResponse> {
        val token = tokenService.recoverToken(request)
        val login = tokenService.validate(token)
        val user = authUseCase.findUserByLogin(login) ?: return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(null)

        val response = customerUseCase.registerCustomer(customer, user.uuid!!)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PutMapping("/update")
    fun updateCustomer(
        @RequestBody customer: Customer,
        request: HttpServletRequest
    ): ResponseEntity<CustomerResponse> {
        val token = tokenService.recoverToken(request)
        val login = tokenService.validate(token)
        val user = authUseCase.findUserByLogin(login) ?: return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(null)

        val response = customerUseCase.updateCustomer(customer, user.uuid!!)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/approval/{customerUUID}")
    fun approvalCustomer(
        @RequestParam(value = "status", required = true) status: CustomerStatus,
        @PathVariable(name = "customerUUID", required = true) customerUUID: UUID,
        request: HttpServletRequest
    ): ResponseEntity<ApprovalCustomerResponse> {
        val token = tokenService.recoverToken(request)
        val login = tokenService.validate(token)
        val user = authUseCase.findUserByLogin(login) ?: return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(null)

        val response = customerUseCase.approvalCustomer(status, customerUUID, user.uuid!!)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/customerEconomicActivities/{customerUUID}")
    fun listEconomicActivitiesByCustomerUUID(
        @PathVariable(name = "customerUUID", required = true) customerUUID: UUID
    ): ResponseEntity<CustomerEconomicActivities> {
        val response = customerUseCase.listEconomicActivitiesByCustomerUUID(customerUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getCustomerByUIUD")
    fun getCustomerByUUID(
        @RequestParam(value = "uuid", required = true) customerUUID: UUID
    ): ResponseEntity<CustomerResponse> {
        val response = customerUseCase.getCustomerByUUID(customerUUID)

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
    fun getCustomerPagination(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        request: HttpServletRequest
    ): ResponseEntity<CustomerPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = customerUseCase.getCustomerPagination(page, count, params)

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
    fun listSimpleCustomer(): ResponseEntity<SimpleCustomersResponse>{
        val response = customerUseCase.listSimpleCustomer()

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