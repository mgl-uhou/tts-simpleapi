package com.mgl_uhou.plugins

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object CharacterCounter : Table("character_counter_tbl") {
    val id = integer("id").autoIncrement()
    val totalChars = long("used_chars")
    val lastUpdate = date("last_update")

    override val primaryKey = PrimaryKey(id)
}

fun Application.configureDatabase(){
    val dbUrl = environment.config.property("db.url").getString()
    val dbUser = environment.config.property("db.user").getString()
    val dbPassword = environment.config.property("db.password").getString()

    // Connection
    Database.connect(
        url = dbUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )
}
