package ch.grisu118.svn

import io.ktor.application.call
import io.ktor.content.defaultResource
import io.ktor.content.static
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.ValuesMap
import java.io.File
import java.util.*

object Application {
  private val passwdFile = File("/etc/subversion/passwd")
  private lateinit var token: String

  @JvmStatic
  fun main(args: Array<String>) {
    token = UUID.randomUUID().toString()
    println("Token: $token")
    val server = embeddedServer(Netty, 8080) {
      routing {
        static {
          defaultResource("index.html")
        }
        post("/api/user") {
          val map = call.receive<ValuesMap>()
          if (map["pass"] != map["pass2"]) {
            call.respondText("Passwords do not match!", status = HttpStatusCode.BadRequest)
            return@post
          }
          if (map["pass"] == null || map["pass"]!!.length < 8) {
            call.respondText("Passwords to short", status = HttpStatusCode.BadRequest)
            return@post
          }
          if (map["pass"].isNullOrBlank() || map["username"].isNullOrBlank() || map["token"].isNullOrBlank()) {
            call.respondText("values could not be empty!", status = HttpStatusCode.BadRequest)
            return@post
          }
          if (token != map["token"]) {
            call.respondText("invalid token", status = HttpStatusCode.Forbidden)
            return@post
          }
          if (userExists(map["username"]!!)) {
            call.respondText("user already exists", status = HttpStatusCode.BadRequest)
            return@post
          }
          createUser(map["username"]!!, map["pass"]!!)
          call.respondText("Created User")
        }
      }
    }
    server.start(wait = true)
  }

  private fun userExists(s: String): Boolean = passwdFile.readLines().map { it.split(":")[0] }.any { it == s }

  private fun createUser(name: String, pass: String) {
    Runtime.getRuntime().exec("htpasswd -b /etc/subversion/passwd $name $pass")
  }
}