plugins {
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
    `kotlin-convention`
    `publish-convention`
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
                api(libs.ktor.server.core)
                implementation(project.dependencies.platform(libs.ktor.bom))
                implementation(libs.ktor.server.double.receive)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.server.sse)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk)
                implementation(libs.datafaker)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.mockk)
                implementation(libs.mockk.dsl)
                implementation(libs.kotlinLogging)
            }
        }

        jvmMain {
            dependencies {
                implementation(project.dependencies.platform(libs.jackson.bom))
                implementation(project.dependencies.platform(libs.netty.bom))
                implementation(libs.ktor.serialization.jackson)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.call.logging)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(libs.junit.jupiter.params)
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.1.1")
    implementation("io.ktor:ktor-server-core-jvm:3.1.1")
    implementation("io.ktor:ktor-serialization-jackson-jvm:3.1.1")
}
