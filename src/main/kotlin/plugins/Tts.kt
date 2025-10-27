package com.mgl_uhou.plugins

import com.mgl_uhou.service.TtsService
import io.ktor.server.application.*
import io.ktor.util.*
 
class Tts(val service: TtsService) {
    companion object Plugin : BaseApplicationPlugin<Application, Tts, Tts> {
        override val key = AttributeKey<Tts>("TtsPlugin")
 
        override fun install(pipeline: Application, configure: Tts.() -> Unit): Tts {
            val tts = Tts(TtsService())
            pipeline.monitor.subscribe(ApplicationStopping) {
                tts.service.close()
            }
            return tts
        }
    }
}