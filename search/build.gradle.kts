plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.serialization)
    alias(libs.plugins.dependency.analysis)
    jacoco
}

group = "no.java.conf"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

kotlin {
    jvmToolchain(22)
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
    implementation(libs.arrow.core)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.monitoring)
    implementation(libs.bundles.search)
    implementation(libs.bundles.serialization)
    implementation(libs.kotlin.logging)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.status.pages)
    runtimeOnly(libs.logback.classic)
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.kotlin.test.junit)
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