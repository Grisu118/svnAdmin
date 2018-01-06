import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "ch.grisu118"
version = "0.1.0"

plugins {
  kotlin("jvm") version "1.2.10"
}

repositories {
  mavenCentral()
  maven { setUrl("https://dl.bintray.com/kotlin/ktor") }
  maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
  maven { setUrl("https://dl.bintray.com/grisu118/kotlin") }
}

dependencies {
  compile(kotlin("stdlib-jdk8", version = "1.2.10"))
  compile(kotlin("stdlib-jre8", version = "1.2.10"))
  compile(kotlin("reflect", version = "1.2.10"))

  compile("ch.grisu118:kotlin-wrapper:0.4.0")

  compile("io.ktor:ktor-server-netty:0.9.0")
  compile("io.ktor:ktor-jackson:0.9.0")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.2")
  compile("ch.qos.logback:logback-classic:1.2.3")
}

kotlin {
  experimental.coroutines = Coroutines.ENABLE
}

val fatJar = task("fatJar", type = Jar::class) {
  group = "build"
  baseName = "${project.name}-fat"
  manifest {
    attributes["Implementation-Title"] = "SvnAdmin"
    attributes["Implementation-Version"] = version
    attributes["Main-Class"] = "ch.grisu118.svn.Application"
  }
  from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
  with(tasks["jar"] as CopySpec)
}

val versionNumberJson = task("versionNumberJson") {
  group = "build"
  val f = File("src/main/resources/static/version.json")
  f.writeText("{\"version\": \"" + project.version + "\"}")
}

val buildFrontend = task("buildFrontend", type = Exec::class) {
  group = "build"
  workingDir = File("frontend")
  commandLine = listOf("npm.cmd", "run", "build")
}

val copyFrontend = task("copyFrontend", type = Copy::class) {
  group = "build"
  from("frontend/build")
  into("src/main/resources/react")
  dependsOn(buildFrontend)
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
  dependsOn(versionNumberJson)
  dependsOn(copyFrontend)
}