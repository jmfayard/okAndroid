package urlalias

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.experimental.*
import java.text.*

data class Model(val name: String, val items: List<Item>)
data class Item(val key: String, val value: String)

/*
         > curl -v --compress --header "Accept: application/json" http://localhost:8080/v1
         {"name":"root","items":[{"key":"A","value":"Apache"},{"key":"B","value":"Bing"}]}
         The result is pretty printed, to show off how to configure gson, but it is
         possible to use the default gson as well

         > curl -v --compress --header "Accept: application/json" http://localhost:8080/v1/item/A
         {"key":"A","value":"Apache"}
     */

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
        }
    }
    server.start(wait = true)
}


fun Application.urlalias() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    val model = Model("root", listOf(Item("A", "Apache"), Item("B", "Bing")))
    routing {
        get("/v1") {
            call.respond(model)
        }
        get("/v1/item/{key}") {
            val item = model.items.firstOrNull { it.key == call.parameters["key"] }
            if (item == null)
                call.respond(HttpStatusCode.NotFound)
            else
                call.respond(item)
        }
    }
}

