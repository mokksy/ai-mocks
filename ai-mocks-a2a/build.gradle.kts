plugins {
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
}

dokka {
    dokkaSourceSets.configureEach {
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":ai-mocks-core"))
                api(project(":ai-mocks-a2a-models"))
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.server.sse)
                api(libs.ktor.client.core)
                api(libs.ktor.client.logging)
                api(libs.ktor.client.content.negotiation)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinLogging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.mockk)
                implementation(libs.mockk.dsl)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.serialization.jackson)
                api(libs.kotlinx.coroutines.reactor)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertj.core)
                implementation(libs.ktor.client.java)
                implementation(libs.awaitility.kotlin)
                implementation(libs.junit.jupiter.params)
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
