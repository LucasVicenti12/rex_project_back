package com.delice.crm.core.roles.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val ROLE_UNEXPECTED_ERROR = RoleException("ROLE_UNEXPECTED_ERROR", "An unexpected error has occurred")

val ROLE_NOT_FOUND = RoleException("ROLE_NOT_FOUND", "This role not found")
val ROLES_IS_EMPTY = RoleException("ROLES_IS_EMPTY", "There no roles available")
val ROLE_ALREADY_EXISTS = RoleException("ROLE_ALREADY_EXISTS", "This role already exists")
val ROLE_CODE_IS_EMPTY = RoleException("ROLE_CODE_IS_EMPTY", "The role code must be provided")
val ROLE_LABEL_IS_EMPTY = RoleException("ROLE_LABEL_IS_EMPTY", "The role label must be provided")
val ROLE_MODULE_UUID_IS_EMPTY = RoleException("ROLE_MODULE_UUID_IS_EMPTY", "The role module UUID must be provided")

val MODULE_NOT_FOUND = RoleException("MODULE_NOT_FOUND", "This module not found")
val MODULES_IS_EMPTY = RoleException("MODULES_IS_EMPTY", "There no modules available")
val MODULE_ALREADY_EXISTS = RoleException("MODULE_ALREADY_EXISTS", "This module already exists")
val MODULE_CODE_IS_EMPTY = RoleException("MODULE_CODE_IS_EMPTY", "The module code must be provided")
val MODULE_LABEL_IS_EMPTY = RoleException("MODULE_LABEL_IS_EMPTY", "The module label must be provided")

val ROLE_UUID_IS_EMPTY = RoleException("ROLE_UUID_IS_EMPTY", "The role must be provided")
val USER_UUID_IS_EMPTY = RoleException("USER_UUID_IS_EMPTY", "The user must be provided")

val MODULE_WITH_ROLE_ATTACH = RoleException("MODULE_WITH_ROLE_ATTACH", "This module have roles attached")
val ROLE_WITH_PERMISSION_ATTACH = RoleException("ROLE_WITH_PERMISSION_ATTACH", "This role have permissions attached")

class RoleException(code: String, message: String) : DefaultError(code, message)