package com.example.database.images

import kotlinx.serialization.Serializable

@Serializable
class ImageDTO(
    val imageId: String,
    val fileName: String
)