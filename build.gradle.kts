import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "ch.grisu118"
version = "0.1.3"

plugins {
  kotlin("jvm") version "1.2.21"
}

repositories {
  mavenCentral()
  maven { setUrl("https://dl.bintray.com/kotlin/ktor") }
  maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
  maven { setUrl("https://dl.bintray.com/grisu118/kotlin") }
}

val ktorVersion = "0.9.1-alpha-8"

dependencies {
  compile(kotlin("stdlib-jdk8", version = "1.2.21"))
  compile(kotlin("stdlib-jre8", version = "1.2.21"))
  compile(kotlin("reflect", version = "1.2.21"))

  compile("ch.grisu118:kotlin-wrapper:0.6.0")

  compile("io.ktor:ktor-server-netty:$ktorVersion")
  compile("io.ktor:ktor-jackson:$ktorVersion")
  compile("io.ktor:ktor-locations:$ktorVersion")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.2")

  compile("io.github.microutils:kotlin-logging:1.5.3")
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
    attributes["Main-Class"] = "io.ktor.server.netty.DevelopmentEngine"
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
  doFirst {
    File("src/main/resources/react").deleteRecursively()
  }
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

val createRelease = task("createRelease") {
  group = "build"
  dependsOn(fatJar)
}