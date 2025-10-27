package com.mgl_uhou.service

import com.google.cloud.texttospeech.v1.*
import com.google.protobuf.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger

class TtsService(
    private val log: Logger
) {
    private val ttsClient: TextToSpeechClient = TextToSpeechClient.create()

    suspend fun synthesizeText(
        text: String,
        languageCode: String,
        voiceName: String?,
        ssmlGender: String
    ): ByteString {
        return withContext(Dispatchers.IO) {
            val input = SynthesisInput.newBuilder().setText(text).build()

            val voiceBuilder = VoiceSelectionParams.newBuilder()
                .setLanguageCode(languageCode)

            // O nome da voz (voiceName) tem prioridade sobre o gênero, pois é mais específico.
            if (!voiceName.isNullOrBlank()) {
                voiceBuilder.setName(voiceName)
                // Log de aviso para o desenvolvedor se ambos forem fornecidos
                if (ssmlGender.uppercase() != "NEUTRAL") {
                    log.info("Both 'voiceName' and 'ssmlGender' were provided. Prioritizing 'voiceName': $voiceName.")
                }
            } else {
                try {
                    val gender = SsmlVoiceGender.valueOf(ssmlGender.uppercase())
                    voiceBuilder.setSsmlGender(gender)
                } catch (e: IllegalArgumentException) {
                    // Se o gênero for inválido, usa o padrão NEUTRAL.
                    voiceBuilder.setSsmlGender(SsmlVoiceGender.NEUTRAL)
                }
            }

            val audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build()

            val response = ttsClient.synthesizeSpeech(input, voiceBuilder.build(), audioConfig)
            response.audioContent
        }
    }

    fun close() {
        ttsClient.close()
    }
}