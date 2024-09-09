plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.serialization)
}

group = "no.java.conf"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.logback.classic)
    implementation(libs.bundles.monitoring)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.ktor.client)
    implementation(libs.arrow.core)
    implementation(libs.kotlin.logging)
    implementation(libs.bundles.search)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs = listOf("-Xcontext-receivers")
        }
    }
}

tasks.shadowJar {
    dependsOn(":web:npm_run_build")

    from("web/dist") {
        into("static")
    }

    archiveFileName.set("history.jar")
}

tasks.jar {
    enabled = false
}