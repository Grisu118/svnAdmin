package ch.grisu118.svn

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.install
import io.ktor.content.defaultResource
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import java.util.*

object Application {
  internal lateinit var token: String

  @JvmStatic
  fun main(args: Array<String>) {
    token = UUID.randomUUID().toString()
    println("Token: $token")

    val server = embeddedServer(Netty, 8080) {
      install(DefaultHeaders)
      install(Compression)
      install(CallLogging)
      install(ContentNegotiation) {
        jackson {
          configure(SerializationFeature.INDENT_OUTPUT, true)
          registerModule(JavaTimeModule())
          registerKotlinModule()
        }
      }
      install(Sessions) {
        cookie<SvnAdminSession>("SvnAdminSession")
      }

      routing {
        static {
          resources("static")
          resources("react")
          defaultResource("react/index.html")
        }
        post("/api/user") {
          handleRegister()
        }
        get("/api/login") {
          handleGetLogin()
        }
        post("/api/login") {
          doLogin()
        }
        post("/api/logout") {
          doLogout()
        }
      }
    }
    server.start(wait = true)
  }

}