package ch.grisu118.svn

import ch.grisu118.kotlin.process.execute
import ch.grisu118.kotlin.security.BCrypt
import io.ktor.auth.Principal
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import java.io.File

class Passwd(private val passwdFile: File) {

  fun authenticate(creds: UserPasswordCredential): Principal? {
    if (creds.name.isBlank() || creds.password.isBlank()) {
      return null
    }
    val hash = getHashForUser(creds.name.trim()) ?: return null
    return if (BCrypt.checkpw(creds.password, hash)) {
      UserIdPrincipal(creds.name)
    } else {
      null
    }
  }

  fun userExists(s: String): Boolean = passwdFile.readLines().map {
    it.split(":")[0]
  }.any { it == s }

  private fun getHashForUser(s: String): String? = passwdFile.readLines().map {
    it.split(":")
  }.firstOrNull { it[0] == s }?.get(1)

  fun createUser(name: String, pass: String) {
    "htpasswd -bB /etc/subversion/passwd $name $pass".execute()
  }
}