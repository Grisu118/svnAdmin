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
import mu.KotlinLogging
import java.io.File
import java.io.IOException

object RepoManager {
  private val logger = KotlinLogging.logger { }

  fun Route.repoManager(repoRoot: File, admins: List<String>) {
    post<Repo> {
      val session = call.sessions.get<SvnAdminSession>()
      if (session == null) {
        call.respond(HttpStatusCode.Unauthorized.description("Not logged in"))
      } else if (!admins.contains(session.userId)) {
        call.respond(HttpStatusCode.Forbidden.description("Not an admin"))
      } else {
        val (name) = call.receive<RepoData>()
        if (name.isBlank()) {
          call.respondText("Name cannot be empty", status = HttpStatusCode.BadRequest)
          return@post
        }
        if (!name.matches(Regex("\\w+"))) {
          call.respondText("Only [a-zA-Z0-9_] are allowed", status = HttpStatusCode.BadRequest)
          return@post
        }
        val newRepo = File(repoRoot, name)
        try {
          newRepo.canonicalPath
        } catch (e: IOException) {
          call.respondText("Invalid Repo Name", status = HttpStatusCode.BadRequest)
          return@post
        }
        if (newRepo.exists()) {
          call.respondText("Repo already exists", status = HttpStatusCode.BadRequest)
          return@post
        }
        if (newRepo.mkdir()) {
          val createCode = "svnadmin create ${newRepo.absolutePath}".execute()
          val chownCode = "chown -R www-data:subversion ${newRepo.absolutePath}".execute()
          val chmodCode = "chmod -R g+rws ${newRepo.absolutePath}".execute()
          logger.info { "CreateCode: $createCode, chownCode: $chownCode, chmodCode: $chmodCode" }
          if (createCode != 0 || chownCode != 0 || chmodCode != 0) {
            newRepo.deleteRecursively()
            call.respondText("Could not create SVN Repo", status = HttpStatusCode.InternalServerError)
          } else {
            call.respond(HttpStatusCode.OK)
          }
        } else {
          call.respondText("Could not create Folder", status = HttpStatusCode.InternalServerError)
        }
      }
    }
  }
}
