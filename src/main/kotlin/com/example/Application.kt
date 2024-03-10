package com.example

import com.example.database.configureDatabaseRouting
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/online-shop", driver = "org.postgresql.Driver",
        user = "postgres", password = "monvex_")
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    configureSockets()
    configureSerialization()
    configureDatabaseRouting()
//    configureDatabases()
//    configureHTTP()
//    configureRouting()
}
