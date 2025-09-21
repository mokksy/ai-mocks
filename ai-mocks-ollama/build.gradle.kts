plugins {
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
    `shadow-convention`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.datetime)
                api(libs.ktor.serialization.kotlinx.json)
                api(project(":ai-mocks-core"))
                api(project.dependencies.platform(libs.ktor.bom))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinLogging)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.server.netty)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertj.core)
                implementation(libs.awaitility.kotlin)
                implementation(libs.finchly)
                implementation(libs.junit.jupiter.params)
                implementation(libs.langchain4j.kotlin)
                // Note: There's no specific Ollama client in langchain4j or spring-ai yet
                // If/when they become available, they should be added here
                implementation(project.dependencies.platform(libs.langchain4j.bom))
                implementation(project.dependencies.platform(libs.spring.ai.bom))
                implementation(project.dependencies.platform(libs.spring.bom))
                implementation(libs.langchain4j.kotlin)
                implementation(libs.langchain4j.ollama)

                implementation(libs.spring.ai.client.chat)
                implementation(libs.spring.ai.ollama)

                // Ktor client dependencies for HTTP tests
                implementation(libs.ktor.client.java)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)

                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifactId = "${project.name}-standalone"
            artifact(tasks.named("shadowJar")) {
                classifier = ""
                extension = "jar"
            }
            artifact(tasks["jvmSourcesJar"]) {
                classifier = "sources"
            }
        }
    }
}
