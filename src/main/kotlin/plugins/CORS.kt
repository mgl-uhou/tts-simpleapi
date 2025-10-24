package com.mgl_uhou.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Options) // Essential for CORS

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.

        // Restrict
        // allowHost("localhost:3000", schemes = listOf("http", "https"))
    }
}