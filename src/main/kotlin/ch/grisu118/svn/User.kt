package ch.grisu118.svn

import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.location
import io.ktor.locations.post
import io.ktor.locations.url
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.method
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import java.util.*

fun Route.login(passwd: Passwd) {
  location<Login> {
    method(HttpMethod.Post) {
      authentication {
        formAuthentication(Login::userName.name, Login::password.name,
          challenge = FormAuthChallenge.Redirect { call, c -> call.url(Login(c?.name ?: "")) },
          validate = { passwd.authenticate(it) })
      }

      handle {
        val principal = call.principal<UserIdPrincipal>()
        val uuid = UUID.randomUUID()
        call.sessions.set(SvnAdminSession(principal!!.name, uuid))
        Application.sessionIds[principal.name] = uuid
        call.respond(LoginResponse(principal.name))
      }
    }
  }
  post<Logout> {
    val session = call.sessions.get<SvnAdminSession>()
    if (!Application.validSession(session)) {
      call.respond(HttpStatusCode.Forbidden)
    } else {
      Application.sessionIds.remove(session?.userId)
      call.sessions.clear<SvnAdminSession>()
      call.respond(HttpStatusCode.OK)
    }
  }
  post<Register> {
    val (token, userName, pass, pass2) = call.receive<RegisterData>()
    if (Application.token != token) {
      call.respondText("invalid token", status = HttpStatusCode.Forbidden)
      return@post
    }
    if (pass.isBlank() || userName.isBlank()) {
      call.respondText("values could not be empty!", status = HttpStatusCode.BadRequest)
      return@post
    }
    if (!userName.matches(Regex("\\w"))) {
      call.respondText("UserName contains not allowed characters. [a-zA-Z0-9_]", status = HttpStatusCode.BadRequest)
      return@post
    }
    if (pass.length < 6) {
      call.respondText("Passwords to short", status = HttpStatusCode.BadRequest)
      return@post
    }
    if (pass != pass2) {
      call.respondText("Passwords do not match!", status = HttpStatusCode.BadRequest)
      return@post
    }
    if (passwd.userExists(userName)) {
      call.respondText("user already exists", status = HttpStatusCode.BadRequest)
      return@post
    }
    passwd.createUser(userName, pass)
    call.respondText("Created User")
  }
}

data class LoginResponse(val userId: String)

