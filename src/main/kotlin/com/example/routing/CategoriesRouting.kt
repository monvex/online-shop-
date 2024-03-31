package com.example.database

import com.example.database.categories.Categories
import com.example.database.categories.CategoryDTO
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable


@Serializable
data class CategoriesReceiveRemote(
    val title: String
)


fun Application.configureCategoriesRouting() {
    routing {
        authenticate {
            authorized("admin", "manager", "user") {
                get("/categories") {
                    val categoryDTO = Categories.fetchAll()
                    call.respond(categoryDTO)
                }
            }
        }

        authenticate {
            authorized("admin", "manager") {
                post("/categories/add") {
                    val receive = call.receive<CategoriesReceiveRemote>()

                    if (Categories.checkExistence(receive.title)) {
                        call.respond(HttpStatusCode.Conflict, "Такая категория уже существует")
                    }
                    else if (receive.title.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "Название категории не может быть пустым")
                    } else {
                        Categories.insert(
                            CategoryDTO(
                                categoryTitle = receive.title.lowercase()
                            )
                        )
                        call.respond(HttpStatusCode.OK)
                    }
                }

                delete("/categories/{title}") {
                    val title = call.parameters["title"]?.lowercase() ?: throw IllegalArgumentException("Invalid Title")

                    if (!Categories.checkExistence(title)) {
                        call.respond(HttpStatusCode.BadRequest, "Такой категории не существует")
                    } else {
                        Categories.delete(title)
                        call.respond(HttpStatusCode.OK)
                    }
                }

                put("/categories/{title}") {
                    val title = call.parameters["title"]?.lowercase() ?: throw IllegalArgumentException("Invalid Title")
                    val receive = call.receive<CategoriesReceiveRemote>() // То на что меняем

                    if (!Categories.checkExistence(title)) {
                        call.respond(HttpStatusCode.BadRequest, "Категории $title не существует в БД")
                    }
                    else if (Categories.checkExistence(receive.title.lowercase())) {
                        call.respond(HttpStatusCode.Conflict, "Категория ${receive.title} уже существует в БД")
                    } else {
                        Categories.update(title, receive.title.lowercase())
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }



    }
}