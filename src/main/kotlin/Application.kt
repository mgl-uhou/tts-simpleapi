package com.mgl_uhou

import com.mgl_uhou.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureCORS()
    install(DI)

    configureSerialization()
    configureDatabase()
    configureRouting()
}