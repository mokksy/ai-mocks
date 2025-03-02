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
                api(project(":ai-mocks-core"))
                api(libs.ktor.serialization.kotlinx.json)
                implementation(project.dependencies.platform(libs.ktor.bom))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmMain {
            dependencies {
                api(libs.ktor.server.netty)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertj.core)
                implementation(libs.assertk)
                implementation(libs.awaitility.kotlin)
                implementation(libs.junit.jupiter.params)
                implementation(libs.langchain4j.kotlin)
                implementation(libs.langchain4j.openai)
                implementation(libs.openai.java)
                implementation(project.dependencies.platform(libs.spring.bom))
                implementation(project.dependencies.platform(libs.spring.ai.bom))
                implementation(libs.spring.ai.openai)
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
