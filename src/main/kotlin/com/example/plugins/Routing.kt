package com.example.plugins

import com.example.database.configureDatabaseRouting
import com.example.routing.configureAuthRouting
import com.example.routing.configurePurchaseItemsRouting
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JWTTokenService
import com.example.security.token.TokenConfig
import io.ktor.server.application.*

fun Application.configureRouting(config: TokenConfig) {
    val tokenService = JWTTokenService()
    val hashingService = SHA256HashingService()
    configureDatabaseRouting()
    configureAuthRouting(hashingService, tokenService, config)
    configurePurchaseItemsRouting()
}
