package com.example.routing

import com.example.database.items.ItemDTO
import com.example.utils.authorized
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable


@Serializable
data class ShoppingCart(
    val items: List<ItemDTO>
)
fun Application.configurePurchaseItemsRouting() {
    routing {
        authenticate {
            authorized("admin", "manager", "user") {
                post("/purchase") {
                    val shoppingCart = call.receive<ShoppingCart>()
                    val totalPrice = shoppingCart.items.sumOf { it.price }
                    call.respond(HttpStatusCode.OK, "Total price: $totalPrice")
                }
            }
        }
    }
}