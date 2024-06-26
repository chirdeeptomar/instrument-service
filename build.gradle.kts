import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("io.freefair.lombok") version "8.6"
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("com.google.osdetector") version "1.7.0"
}

group = "com.empyrean.hft"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.5.7"
val junitJupiterVersion = "5.11.0-M1"
val arrowVersion = "16.0.0"
val log4jVersion = "3.0.0-beta2"

val mainVerticleName = "com.empyrean.hft.service.instrument.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  if (osdetector.arch.equals("aarch_64")) {
    implementation("io.netty:netty-all")
  }
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation(platform("org.apache.logging.log4j:log4j-bom:$log4jVersion"))

  implementation("org.apache.arrow:arrow-vector:${arrowVersion}")

  implementation("io.vertx:vertx-core")
  implementation("com.ongres.scram:client:2.1")
  implementation("org.apache.logging.log4j:log4j-api")
  implementation("org.apache.logging.log4j:log4j-core")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
