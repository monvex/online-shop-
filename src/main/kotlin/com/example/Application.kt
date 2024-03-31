package com.example

import com.example.plugins.configureHTTP
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.security.token.TokenConfig
import configureJWTAuth
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    Database.connect("jdbc:postgresql://localhost:5432/online-shop", driver = "org.postgresql.Driver",
        user = "postgres", password = "monvex_")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = TokenConfig(
        audience = environment.config.property("jwt.audience").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
//    configureSockets()
    configureSerialization()
    configureHTTP()
    configureJWTAuth(config)
    configureRouting(config)
}
