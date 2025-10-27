package com.mgl_uhou.plugins

import com.mgl_uhou.models.TtsRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    val ttsService = plugin(Tts).service

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

        post("/tts") {
            try {
                val request = call.receive<TtsRequest>()
                val audioBytes = ttsService.synthesizeText(request.text)

                call.response.header(HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "speech.mp3").toString())
                call.respondBytes(audioBytes.toByteArray(), ContentType.Audio.MPEG)
            } catch(e: Exception) {
                call.application.log.error("Failed to process TTS request", e)
                call.respond(HttpStatusCode.InternalServerError, "An error occured during text-to-speech conversion.")
            }
        }
    }
}
