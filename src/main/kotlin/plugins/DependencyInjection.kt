package com.mgl_uhou.plugins

import com.mgl_uhou.controllers.TtsController
import com.mgl_uhou.service.CharacterCounterService
import com.mgl_uhou.service.TtsService
import io.ktor.server.application.*
import io.ktor.util.* // Necessário para AttributeKey
import io.ktor.server.application.hooks.MonitoringEvent

class AppServices(
    val ttsController: TtsController,
    private val ttsService: TtsService // Mantemos para poder chamar o close()
) {
    fun close() {
        ttsService.close()
    }
}

object DI : BaseApplicationPlugin<Application, Unit, AppServices> {
    // A chave é usada para recuperar a instância do plugin mais tarde (e.g., plugin(DI))
    override val key = AttributeKey<AppServices>("AppServicesPlugin")

    // O método install é onde a lógica de criação e configuração do plugin acontece
    // pipeline: A instância da Application onde o plugin está sendo instalado
    // configure: Um lambda para configuração, que é Unit neste caso (não temos configuração específica)
    override fun install(pipeline: Application, configure: Unit.() -> Unit): AppServices {
        // Criar as instâncias dos serviços e do controller
        val ttsService = TtsService(pipeline.log) // Usar pipeline.log para acessar o logger da aplicação
        val characterCounterService = CharacterCounterService()
        val ttsController = TtsController(ttsService, characterCounterService)
        val appServices = AppServices(ttsController, ttsService)

        // Registrar um hook para fechar os recursos quando a aplicação parar
        pipeline.monitor.subscribe(ApplicationStopping) {
            appServices.close()
            pipeline.log.info("Services stopped and resources released.")
        }

        // Retornar a instância do AppServices, que será o "valor" do nosso plugin
        return appServices
    }
}
