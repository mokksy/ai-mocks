plugins {
    `kotlin-convention`
    `publish-convention`
    alias(libs.plugins.kover) apply true
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(project(":ai-mocks-a2a-models"))
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.client.core)
                api(libs.ktor.client.logging)
                api(libs.ktor.client.content.negotiation)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                api(project(":ai-mocks-a2a"))
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinLogging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.mockk)
                implementation(libs.mockk.dsl)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.java)
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
