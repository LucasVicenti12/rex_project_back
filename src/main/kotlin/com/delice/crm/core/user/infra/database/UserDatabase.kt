package com.delice.crm.core.user.infra.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object UserDatabase: Table("users") {
    var uuid = uuid("uuid").uniqueIndex()
    var login = varchar("login", 60).uniqueIndex()
    var password = text("password")
    var userType = text("user_type")
    var email = varchar("email", 90).nullable()
    var avatar = blob("avatar_image").nullable()
    var status = integer("status")
    var name = varchar("name", 20)
    var surname = varchar("surname", 50)
    var document = varchar("document", 11).uniqueIndex()
    var phone = varchar("phone", 20).nullable()
    var dateOfBirth = date("date_of_birth")
    var state = char("state", 2)
    var city = varchar("city", 60)
    var zipCode = varchar("zip_code", 8).nullable()
    var createdAt = datetime("created_at")
    var modifiedAt = datetime("modified_at")
}