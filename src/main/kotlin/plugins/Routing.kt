package com.mgl_uhou.plugins

import com.mgl_uhou.controllers.TtsController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting(ttsController: TtsController) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/test-db") {
            val results = transaction {
                CharacterCounter.selectAll().map {
                    CharacterCounterEntry(
                        id = it[CharacterCounter.id],
                        totalChars = it[CharacterCounter.usedChars],
                        lastUpdate = it[CharacterCounter.lastUpdate].toString()
                    )
                }
            }
            call.respond(results)
        }

        post("/tts") {
            ttsController.processAndRespond(call)
        }
    }
}
