package com.mgl_uhou.service

import com.google.cloud.texttospeech.v1.*
import com.google.protobuf.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TtsService {
    private val ttsClient: TextToSpeechClient = TextToSpeechClient.create()

    suspend fun synthesizeText(text: String): ByteString {
        return withContext(Dispatchers.IO) {
            val input = SynthesisInput.newBuilder().setText(text).build()

            val voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("pt-BR")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build()

            val audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build()

            val response = ttsClient.synthesizeSpeech(input, voice, audioConfig)
            response.audioContent
        }
    }

    fun close() {
        ttsClient.close()
    }
}