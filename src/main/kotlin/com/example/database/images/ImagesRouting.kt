package com.example.database.images


import com.example.database.images.Images.delete
import com.example.database.images.Images.fetchAll
import com.example.database.images.Images.insert
import com.example.database.images.Images.update
import com.example.database.images.Images.uploadImage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Application.configureImagesRouting() {
    routing {
        post("/images/upload") {
            runCatching {
                val multipartData = call.receiveMultipart()
                val result = uploadImage(multipartData)
                if (result != null) {
                    insert(result.imageId, result.fileName)

                    call.respond(HttpStatusCode.OK, result)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Файл с таким названием уже существует")
                }
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, it.message ?: "Something went wrong")
            }
        }

        get("/images/{id}") {
            kotlin.runCatching {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing parameter: fileName")
                val imageFile = File(Images.fetch(id))
                if (imageFile.exists()) {
                    call.respond(LocalFileContent(imageFile))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }

        get("/images") {
            kotlin.runCatching {
                val images = fetchAll()
                if (images.isNotEmpty()) {
                    val imagesIds: MutableList<String> = mutableListOf()
                    images.forEach {
                        imagesIds.add(it.imageId)
                    }
                    call.respond(imagesIds)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Ни одной фотографии не найдено")
                }
            }
        }

        delete("/images/{id}") {
            kotlin.runCatching {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing parameter: fileName")
                val imageFile = File(Images.fetch(id))
                if (imageFile.exists()) {
                    delete(id)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Файла с таким id не существует")
                }
            }
        }

        put("/images/{id}") {
            kotlin.runCatching {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing parameter: fileName")
                val imageFile = File(Images.fetch(id))
                if (imageFile.exists()) {
                    val multipartData = call.receiveMultipart()
                    val result = update(id, multipartData)
                    if (result != null)
                        call.respond(result)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Файла с таким id не существует")
                }
            }
        }
    }
}