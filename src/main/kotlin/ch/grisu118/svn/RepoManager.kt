package ch.grisu118.svn

import ch.grisu118.kotlin.process.execute
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import java.io.File
import java.io.IOException

// TODO config
val repoRoot = File("/srv/svn")

internal fun Route.repoManager() {
  post<Repo> {
    val session = call.sessions.get<SvnAdminSession>()
    if (session == null) {
      call.respond(HttpStatusCode.Unauthorized.description("Not logged in"))
    } else {
      val (name) = call.receive<RepoData>()
      if (name.isBlank()) {
      call.respondText("Name cannot be empty", status = HttpStatusCode.BadRequest)
        return@post
    }
      val newRepo = File(repoRoot, name)
    try {
      newRepo.canonicalPath
    } catch (e: IOException) {
      e.printStackTrace()
      call.respondText("Invalid Repo Name", status = HttpStatusCode.BadRequest)
      return@post
    }
    if (newRepo.exists()) {
      call.respondText("Repo already exists", status = HttpStatusCode.BadRequest)
      return@post
    }
    newRepo.mkdir()
    "svnadmin create ${newRepo.absolutePath}".execute()
    "chown -R www-data:subversion ${newRepo.absolutePath}".execute()
    "chmod -R g+rws ${newRepo.absolutePath}".execute()
    call.respond(HttpStatusCode.OK)
    }
  }
}
