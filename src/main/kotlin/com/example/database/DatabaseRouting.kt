package com.example.database

import com.example.routing.configureImagesRouting
import com.example.routing.configureItemsRouting
import io.ktor.server.application.*





fun Application.configureDatabaseRouting() {
    configureBrandsRouting()
    configureCategoriesRouting()
    configureItemsRouting()
    configureImagesRouting()
}