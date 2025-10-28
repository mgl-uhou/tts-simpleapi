package com.mgl_uhou.plugins

import com.mgl_uhou.controllers.TtsController
import com.mgl_uhou.service.CharacterCounterService
import com.mgl_uhou.service.TtsService
import io.ktor.server.application.*

class AppServices(
    val ttsController: TtsController,
    private val ttsService: TtsService // Mantemos para poder chamar o close()
) {
    fun close() {
        ttsService.close()
    }
}

val DI = createApplicationPlugin(name = "DependencyInjection") {
    // Este bloco é para configuração, que não existe.
    // A lógica de criação vai para o `install` abaixo.
}

fun Application.installDI(): AppServices {
    val ttsService = TtsService(log)
    val characterCounterService = CharacterCounterService()
    val ttsController = TtsController(ttsService, characterCounterService)
    val appServices = AppServices(ttsController, ttsService)

    monitor.subscribe(ApplicationStopping) {
        appServices.close()
        log.info("Services stopped and resources released.")
    }
    return appServices
}