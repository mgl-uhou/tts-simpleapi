package com.mgl_uhou.plugins

import io.ktor.server.application.Application
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

@Serializable
data class CharacterCounterEntry(val id: Int, val totalChars: Long, val lastUpdate: String)

object CharacterCounter : Table("character_counter_tbl") {
    val id = integer("id").autoIncrement()
    val usedChars = long("used_chars")
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
