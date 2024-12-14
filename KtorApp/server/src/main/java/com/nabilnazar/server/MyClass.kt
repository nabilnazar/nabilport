package com.nabilnazar.server


import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.delete
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import io.ktor.serialization.kotlinx.json.json


@Serializable
data class Item(val id: Int, val name: String, val price: Double)

// In-memory storage for items
val itemList = mutableListOf(
    Item(1, "Laptop", 1500.0),
    Item(2, "Smartphone", 700.0),
    Item(3, "Tablet", 500.0)
)

fun Application.module() {
    // Install ContentNegotiation for JSON serialization
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Hello, World!")
        }

        route("/items") {
            get { // Get all items
                call.respond(itemList)
            }

            get("/{id}") { // Get item by ID
                val id = call.parameters["id"]?.toIntOrNull()
                val item = itemList.find { it.id == id }
                if (item != null) {
                    call.respond(item)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Item not found")
                }
            }

            post { // Add a new item
                val newItem = call.receive<Item>()
                val nextId = (itemList.maxOfOrNull { it.id } ?: 0) + 1
                val itemWithId = newItem.copy(id = nextId) // Auto-increment ID
                itemList.add(itemWithId)
                call.respond(HttpStatusCode.Created, itemWithId)
            }

            put("/{id}") { // Update an item
                val id = call.parameters["id"]?.toIntOrNull()
                val updatedItem = call.receive<Item>()
                val index = itemList.indexOfFirst { it.id == id }
                if (index != -1) {
                    itemList[index] = updatedItem.copy(id = id!!)
                    call.respond(HttpStatusCode.OK, itemList[index])
                } else {
                    call.respond(HttpStatusCode.NotFound, "Item not found")
                }
            }

            delete("/{id}") { // Delete an item
                val id = call.parameters["id"]?.toIntOrNull()
                val removed = itemList.removeIf { it.id == id }
                if (removed) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Item not found")
                }
            }
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}