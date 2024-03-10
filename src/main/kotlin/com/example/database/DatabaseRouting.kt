package com.example.database

import com.example.database.images.configureImagesRouting
import com.example.database.items.configureItemsRouting
import io.ktor.server.application.*





fun Application.configureDatabaseRouting() {
    configureBrandsRouting()
    configureCategoriesRouting()
    configureItemsRouting()
    configureImagesRouting()
}