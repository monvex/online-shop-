package com.example.security.hashing

data class SaltedHash (
    var hash: String,
    var salt: String
)