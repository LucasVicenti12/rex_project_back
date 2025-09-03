package com.delice.crm.modules.customer.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.customer.domain.entities.Customer
import com.delice.crm.modules.customer.domain.entities.CustomerStatus
import com.delice.crm.modules.customer.domain.usecase.CustomerUseCase
import com.delice.crm.modules.customer.domain.usecase.response.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/customer")
class CustomerWebService(private val customerUseCase: CustomerUseCase) {
    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('CREATE_CUSTOMER', 'ALL_CUSTOMER')")
    fun registerCustomer(@RequestBody customer: Customer): ResponseEntity<CustomerResponse> {
        val user = getCurrentUser()

        val response = customerUseCase.registerCustomer(customer, user.uuid)

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
    @PreAuthorize("hasAnyAuthority('CREATE_CUSTOMER', 'ALL_CUSTOMER')")
    fun updateCustomer(@RequestBody customer: Customer): ResponseEntity<CustomerResponse> {
        val user = getCurrentUser()

        val response = customerUseCase.updateCustomer(customer, user.uuid)

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
    @PreAuthorize("hasAnyAuthority('APPROVAL_CUSTOMER', 'ALL_CUSTOMER')")
    fun approvalCustomer(
        @RequestParam(value = "status", required = true) status: CustomerStatus,
        @PathVariable(name = "customerUUID", required = true) customerUUID: UUID
    ): ResponseEntity<ApprovalCustomerResponse> {
        val user = getCurrentUser()

        val response = customerUseCase.approvalCustomer(status, customerUUID, user.uuid)

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
    @PreAuthorize("hasAnyAuthority('READ_CUSTOMER', 'ALL_CUSTOMER')")
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
    @PreAuthorize("hasAnyAuthority('READ_CUSTOMER', 'ALL_CUSTOMER')")
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
    @PreAuthorize("hasAnyAuthority('READ_CUSTOMER', 'ALL_CUSTOMER')")
    fun getCustomerPagination(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        @RequestParam(
            value = "orderBy",
            required = false
        ) orderBy: OrderBy,
        request: HttpServletRequest
    ): ResponseEntity<CustomerPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = customerUseCase.getCustomerPagination(page, count, orderBy, params)

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
    fun listSimpleCustomer(): ResponseEntity<SimpleCustomersResponse> {
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