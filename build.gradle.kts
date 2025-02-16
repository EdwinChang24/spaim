plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
}

group = "dev.edwinchang"
version = "0.0.1"

repositories { mavenCentral() }

dependencies {
    implementation(libs.ktor.client)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.slf4j.nop)
}

kotlin { compilerOptions { freeCompilerArgs.add("-Xmulti-dollar-interpolation") } }
