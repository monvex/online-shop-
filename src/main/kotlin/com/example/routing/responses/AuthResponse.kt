package com.example.routing.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val token: String,
    val role: String
)