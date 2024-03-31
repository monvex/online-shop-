package com.example.database.users

import kotlinx.serialization.Serializable

@Serializable
class UserDTO(
    val id: Int,
    val username: String,
    val password: String,
    val salt: String,
    val role: String
)

data class User(
    val username: String,
    val password: String,
    val salt: String,
    val role: String
)