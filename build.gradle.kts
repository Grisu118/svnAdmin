import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "ch.grisu118"
version = "1.0-SNAPSHOT"

plugins {
  kotlin("jvm") version "1.2.10"
}

repositories {
  mavenCentral()
  maven { setUrl("http://dl.bintray.com/kotlin/ktor") }
  maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
  compile(kotlin("stdlib-jdk8"))
  compile("io.ktor:ktor-server-netty:0.9.0")
  compile("ch.qos.logback:logback-classic:1.2.3")
}

kotlin {
  experimental.coroutines = Coroutines.ENABLE
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
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