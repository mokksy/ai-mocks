plugins {
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
    `netty-convention`
    `shadow-convention`
    // id("org.openapi.generator") version "7.12.0"
}

dokka {
    dokkaSourceSets.configureEach {
    }
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
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
                implementation(libs.langchain4j.openai)
                implementation(libs.openai.java)
                implementation(libs.spring.ai.client.chat)
                implementation(libs.spring.ai.openai)
                implementation(project.dependencies.platform(libs.langchain4j.bom))
                implementation(project.dependencies.platform(libs.spring.ai.bom))
                implementation(project.dependencies.platform(libs.spring.bom))
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
