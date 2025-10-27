package com.mgl_uhou.controllers

import com.mgl_uhou.exceptions.LimitExceededException
import com.mgl_uhou.models.TtsRequest
import com.mgl_uhou.service.CharacterCounterService
import com.mgl_uhou.service.TtsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class TtsController(
    private val ttsService: TtsService,
    private val characterCounterService: CharacterCounterService,
) {
    suspend fun processAndRespond(call: ApplicationCall) {
        try {
            val request = call.receive<TtsRequest>()
            val charCount = request.text.length

            characterCounterService.validateAndIncrement(charCount)

            val audioBytes = ttsService.synthesizeText(
                text = request.text,
                languageCode = request.languageCode,
                voiceName = request.voiceName,
                ssmlGender = request.ssmlGender
            )

            val safeFileName = request.fileName.takeIf { it.endsWith(".mp3", ignoreCase = true) } ?: "${request.fileName}.mp3"
            call.response.header(HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName, safeFileName).toString())
            call.respondBytes(audioBytes.toByteArray(), ContentType.Audio.MPEG)
        } catch(e: LimitExceededException) {
            call.application.log.warn("TTS request blocked: ${e.message}")
            call.respond(HttpStatusCode.Forbidden, e.message ?: "Request limit reached.")
        }
    }
}