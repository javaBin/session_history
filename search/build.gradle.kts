plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.serialization)
    jacoco
}

group = "no.java.conf"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

kotlin {
    jvmToolchain(22)

    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
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
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.logback.classic)
    implementation(libs.bundles.monitoring)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.ktor.client)
    implementation(libs.arrow.core)
    implementation(libs.kotlin.logging)
    implementation(libs.bundles.search)
    testImplementation(libs.bundles.test)
}

tasks.shadowJar {
    archiveFileName.set("search.jar")
}

tasks.jar {
    enabled = false
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}