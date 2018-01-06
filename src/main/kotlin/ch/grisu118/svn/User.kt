package ch.grisu118.svn

import ch.grisu118.kotlin.process.execute
import ch.grisu118.kotlin.security.BCrypt
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineContext
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import java.io.File

private val passwdFile = File("/etc/subversion/passwd")

internal suspend fun PipelineContext<Unit, ApplicationCall>.handleRegister() {
  val map = call.receive<Map<String, String>>()
  if (map["pass"] != map["pass2"]) {
    call.respondText("Passwords do not match!", status = HttpStatusCode.BadRequest)
    return
  }
  if (map["pass"] == null || map["pass"]!!.length < 6) {
    call.respondText("Passwords to short", status = HttpStatusCode.BadRequest)
    return
  }
  if (map["pass"].isNullOrBlank() || map["username"].isNullOrBlank() || map["token"].isNullOrBlank()) {
    call.respondText("values could not be empty!", status = HttpStatusCode.BadRequest)
    return
  }
  if (Application.token != map["token"]) {
    call.respondText("invalid token", status = HttpStatusCode.Forbidden)
    return
  }
  if (userExists(map["username"]!!)) {
    call.respondText("user already exists", status = HttpStatusCode.BadRequest)
    return
  }
  createUser(map["username"]!!, map["pass"]!!)
  call.respondText("Created User")
}

internal suspend fun PipelineContext<Unit, ApplicationCall>.doLogin() {
  val map = call.receive<Map<String, String>>()
  val userName = map["username"]
  val pass = map["pass"]
  if (userName.isNullOrBlank() || pass.isNullOrBlank()) {
    call.respondText("values could not be empty!", status = HttpStatusCode.BadRequest)
    return
  }
  val hash = getHashForUser(userName!!.trim())
  if (hash == null) {
    call.respondText("User not found", status = HttpStatusCode.NotFound)
    return
  }
  if (BCrypt.checkpw(pass, hash)) {
    call.sessions.set(SvnAdminSession(userId = userName))
    call.respond(LoginResponse(userName))
  } else {
    call.respondText("Invalid Password", status = HttpStatusCode.Unauthorized)
    return
  }
}

internal suspend fun PipelineContext<Unit, ApplicationCall>.handleGetLogin() {
  val user = call.sessions.get<SvnAdminSession>()
  if (user == null) {
    call.respond(HttpStatusCode.Forbidden)
  } else {
    call.respond(LoginResponse(user.userId))
  }
}

internal suspend fun PipelineContext<Unit, ApplicationCall>.doLogout() {
  val user = call.sessions.get<SvnAdminSession>()
  if (user == null) {
    call.respond(HttpStatusCode.Forbidden)
  } else {
    call.sessions.clear<SvnAdminSession>()
    call.respond(HttpStatusCode.OK)
  }
}

data class LoginResponse(val userId: String)

private fun userExists(s: String): Boolean = passwdFile.readLines().map { it.split(":")[0] }.any { it == s }

private fun getHashForUser(s: String): String? = passwdFile.readLines().map {
  it.split(":")
}.firstOrNull { it[0] == s }?.get(1)

private fun createUser(name: String, pass: String) {
  "htpasswd -bB /etc/subversion/passwd $name $pass".execute()
}