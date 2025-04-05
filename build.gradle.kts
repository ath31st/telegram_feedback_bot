val tgbotapiVersion: String by project
val slf4jApiVersion: String by project
val classicLogbackVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.1.10"
}

group = "sidim.doma"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.inmo:tgbotapi-jvm:$tgbotapiVersion")
    implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
    implementation("ch.qos.logback:logback-classic:$classicLogbackVersion")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("standalone")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}