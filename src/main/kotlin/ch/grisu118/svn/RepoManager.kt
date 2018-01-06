package ch.grisu118.svn

import ch.grisu118.kotlin.process.execute
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineContext
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import java.io.File
import java.io.IOException

// TODO config
val repoRoot = File("/srv/svn")

internal suspend fun PipelineContext<Unit, ApplicationCall>.doCreate() {
  val session = call.sessions.get<SvnAdminSession>()
  if (!Application.validSession(session)) {
    call.respond(HttpStatusCode.Unauthorized)
  } else {
    val params = call.receive<Create>()
    if (params.name.isNullOrBlank()) {
      call.respondText("Name cannot be empty", status = HttpStatusCode.BadRequest)
      return
    }
    val newRepo = File(repoRoot, params.name)
    try {
      newRepo.canonicalPath
    } catch (e: IOException) {
      e.printStackTrace()
      call.respondText("Invalid Repo Name", status = HttpStatusCode.BadRequest)
      return
    }
    if (newRepo.exists()) {
      call.respondText("Repo already exists", status = HttpStatusCode.BadRequest)
      return
    }
    newRepo.mkdir()
    "svnadmin create ${newRepo.absolutePath}".execute()
    "chown -R www-data:subversion ${newRepo.absolutePath}".execute()
    "chmod -R g+rws ${newRepo.absolutePath}".execute()
    call.respond(HttpStatusCode.OK)
  }
}

data class Create(val name: String?)