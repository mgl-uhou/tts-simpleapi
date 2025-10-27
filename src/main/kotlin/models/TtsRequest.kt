package com.mgl_uhou.models

import kotlinx.serialization.Serializable

@Serializable
data class TtsRequest(
    val text: String,
    val languageCode: String = "pt-BR",
    val voiceName: String? = null,
    val ssmlGender: String = "NEUTRAL", // Male or Female or Neutral
    val fileName: String = "speech.mp3"
)