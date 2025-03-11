package com.delice.crm.core.user.infra.database

import org.jetbrains.exposed.sql.Table

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
    var document = varchar("document", 11).nullable()
}