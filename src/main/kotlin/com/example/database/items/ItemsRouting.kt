package com.example.database.items

import com.example.database.brands.Brands
import com.example.database.categories.Categories
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ItemReceiveRemote(
    val itemTitle: String,
    val brand: String,
    val category: String
)

fun Application.configureItemsRouting() {
    routing {
        get("/items") {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val pageSize = call.request.queryParameters["size"]?.toInt() ?: 2
            val itemsDTO = Items.fetchPaginatedItems(page, pageSize)
            call.respond(itemsDTO)
        }

        post("/items/add") {
            val receive = call.receive<ItemReceiveRemote>()
            val exists = Items.checkExistence(receive.itemTitle)
            val brandExists = Brands.checkExistence(receive.brand)
            val categoryExists = Categories.checkExistence(receive.category)

            if (exists) {
                call.respond(HttpStatusCode.Conflict, "Такой товар уже существует")
            } else if(!brandExists || !categoryExists) {
                call.respond(HttpStatusCode.BadRequest, "Такой категории или такого бренда не существует")
            } else if (receive.itemTitle.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Название категории не может быть пустым")
            } else {
                Items.insert(
                    ItemReceiveRemote(
                        itemTitle = receive.itemTitle.lowercase(),
                        brand = receive.brand.lowercase(),
                        category = receive.category.lowercase()
                    )
                )
                call.respond(HttpStatusCode.OK)
            }
        }

        delete("/items/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid Title")

            if (!Items.checkExistence(id)) {
                call.respond(HttpStatusCode.BadRequest, "Такого товара не существует")
            } else {
                Items.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }

        put("/items/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid Title")
            val receive = call.receive<ItemDTO>() // То на что меняем

            if (!Items.checkExistence(receive.id)) {
                call.respond(HttpStatusCode.BadRequest, "Объекта с id=${receive.id} не существует в БД")
            } else {
                Items.update(id, receive)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}