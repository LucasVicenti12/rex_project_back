package com.delice.crm.core.roles.infra.web

import com.delice.crm.core.roles.domain.entities.Module
import com.delice.crm.core.roles.domain.entities.Role
import com.delice.crm.core.roles.domain.usecase.RolesUseCase
import com.delice.crm.core.roles.domain.usecase.response.*
import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.core.utils.ordernation.OrderBy
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/roles")
class RolesWebService(
    private val rolesUseCase: RolesUseCase,
) {
    @GetMapping("/allRoles")
    @PreAuthorize("hasAnyAuthority('ATTACH_ROLES', 'ALL_USER')")
    fun getRoles(): ResponseEntity<RoleByModuleResponse> {
        val response = rolesUseCase.getAllRolesByModule()

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/allModules")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun getModules(): ResponseEntity<ModuleListResponse> {
        val response = rolesUseCase.getModules()

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getModulesPagination")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun getModulesPagination(
        @RequestParam(
            value = "page",
            required = true
        ) page : Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        @RequestParam(
            value = "orderBy",
            required = false
        ) orderBy : OrderBy,
        request: HttpServletRequest
    ): ResponseEntity<ModulePaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = rolesUseCase.getModulesPagination(page, count, orderBy, params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/rolesPerUser")
    @PreAuthorize("hasAnyAuthority('ATTACH_ROLES', 'ALL_USER')")
    fun getRolesPerUser(
        @RequestParam(
            value = "uuid",
            required = true
        ) userUUID: UUID
    ): ResponseEntity<RoleListResponse> {
        val response = rolesUseCase.getRolesPerUser(userUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/myRoles")
    fun getMyRoles(
        request: HttpServletRequest
    ): ResponseEntity<RoleListResponse> {
        val user = getCurrentUser()

        val response = rolesUseCase.getRolesPerUser(user.uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/createRole")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun createRole(@RequestBody role: Role): ResponseEntity<RoleResponse> {
        val response = rolesUseCase.createRole(role)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @DeleteMapping("/deleteRole/{uuid}")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun deleteRole(
        @PathVariable(
            name = "uuid",
            required = true
        ) roleUUID: UUID
    ): ResponseEntity<RoleDeleteResponse> {
        val response = rolesUseCase.deleteRole(roleUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/createModule")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun createModule(@RequestBody module: Module): ResponseEntity<ModuleResponse> {
        val response = rolesUseCase.createModule(module)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @DeleteMapping("/deleteModule/{uuid}")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun deleteModule(
        @PathVariable(
            name = "uuid",
            required = true
        ) moduleUUID: UUID
    ): ResponseEntity<ModuleDeleteResponse> {
        val response = rolesUseCase.deleteModule(moduleUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/attach/{uuid}")
    @PreAuthorize("hasAnyAuthority('ATTACH_ROLES', 'ALL_USER')")
    fun attachRole(
        @PathVariable(
            name = "uuid",
            required = true
        ) userUUID: UUID,
        @RequestBody roles: List<UUID>
    ): ResponseEntity<RoleListResponse> {
        val response = rolesUseCase.attachRole(userUUID, roles)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getModuleByUUID/{uuid}")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun getModuleByUUID(
        @PathVariable(
            name = "uuid",
            required = true
        ) moduleUUID: UUID
    ): ResponseEntity<ModuleResponse> {
        val response = rolesUseCase.getModuleByUUID(moduleUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getRoleByModuleUUID/{uuid}")
    @PreAuthorize("hasAnyAuthority('CREATE_ROLES')")
    fun getRoleByModuleUUID(
        @PathVariable(
            name = "uuid",
            required = true
        ) moduleUUID: UUID
    ): ResponseEntity<ModuleWithRolesResponse> {
        val response = rolesUseCase.getModuleWithRolesByUUID(moduleUUID)

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