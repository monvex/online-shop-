package com.example.database

import com.example.database.brands.BrandDTO
import com.example.database.brands.Brands
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureBrandsRouting() {
    routing {
        get("/brands") {
            val brandsDTO = Brands.fetchAll()
            call.respond(brandsDTO)
        }


        post("/brands/add") {
            val receive = call.receive<CategoriesReceiveRemote>()

            if (Brands.checkExistence(receive.title)) {
                call.respond(HttpStatusCode.Conflict, "Такой бренд уже существует")
            }
            else if (receive.title.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Название бренда не может быть пустым")
            } else {
                Brands.insert(
                    BrandDTO(
                        brandTitle = receive.title.lowercase()
                    )
                )
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/brands/{title}") {
            val title = call.parameters["title"]?.lowercase() ?: throw IllegalArgumentException("Invalid Title")

            if (!Brands.checkExistence(title)) {
                call.respond(HttpStatusCode.BadRequest, "Такого бренда не существует")
            } else {
                Brands.delete(title)
                call.respond(HttpStatusCode.OK)
            }
        }

        put("/brands/{title}") {
            val title = call.parameters["title"]?.lowercase() ?: throw IllegalArgumentException("Invalid Title")
            val receive = call.receive<CategoriesReceiveRemote>() // То на что меняем

            if (!Brands.checkExistence(title)) {
                call.respond(HttpStatusCode.BadRequest, "Бренда $title не существует в БД")
            }
            else if (Brands.checkExistence(receive.title.lowercase())) {
                call.respond(HttpStatusCode.Conflict, "Бренд ${receive.title} уже существует в БД")
            } else {
                Brands.update(title, receive.title.lowercase())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}