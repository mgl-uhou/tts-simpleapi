package com.mgl_uhou.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/test-db") {
            val results = transaction {
                CharacterCounter.selectAll().map {
                    CharacterCounterEntry(
                        id = it[CharacterCounter.id],
                        totalChars = it[CharacterCounter.totalChars],
                        lastUpdate = it[CharacterCounter.lastUpdate].toString()
                    )
                }
            }
            call.respond(results)
        }
    }
}
