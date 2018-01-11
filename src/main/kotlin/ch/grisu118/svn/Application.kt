package ch.grisu118.svn

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.content.defaultResource
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.hex
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Location("/api/repo")
class Repo

data class RepoData(val name: String)

@Location("/api/login")
data class Login(val userName: String = "", val password: String = "")

@Location("/api/logout")
class Logout

@Location("/api/register")
class Register

data class RegisterData(val token: String, val username: String, val pass: String, val pass2: String)

fun Application.svnAdminApplication() {
  Application
  install(Locations)
  install(DefaultHeaders)
  install(Compression)
  install(CallLogging) {
  }
  install(ContentNegotiation) {
    jackson {
      configure(SerializationFeature.INDENT_OUTPUT, true)
      registerModule(JavaTimeModule())
      registerKotlinModule()
    }
  }
  val svnAdminConfig = environment.config.config("svnAdmin")
  val sessionCookieConfig = svnAdminConfig.config("session.cookie")
  val key: String = sessionCookieConfig.property("key").getString()
  val sessionKey = hex(key)

  val authConfig = svnAdminConfig.config("auth")
  val passwdFile = File(authConfig.property("passwdFile").getString())

  install(Sessions) {
    cookie<SvnAdminSession>("SvnAdminSession") {
      transform(SessionTransportTransformerMessageAuthentication(sessionKey))
    }
  }

  install(Routing) {
    login(Passwd(passwdFile))
    repoManager()
    static {
      resources("static")
      resources("react")
      defaultResource("react/index.html")
    }
  }
}

object Application {
  internal val token: String = UUID.randomUUID().toString()
  internal val sessionIds = ConcurrentHashMap<String, UUID>()

  internal fun validSession(session: SvnAdminSession?): Boolean {
    return session != null && sessionIds[session.userId] == session.uuid
  }

  init {
    println("Token: $token")
  }
}