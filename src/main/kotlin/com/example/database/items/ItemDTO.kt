package com.example.database.items

import kotlinx.serialization.Serializable

@Serializable
class ItemDTO(
    val id: Int,
    val itemTitle: String,
    val brand: String,
    val category: String
)