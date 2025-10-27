package com.mgl_uhou.models

import kotlinx.serialization.Serializable

@Serializable
data class TtsRequest (val text: String)