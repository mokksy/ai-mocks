import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
}

kotlin {

    jvmToolchain(17)

    explicitApi()

    withSourcesJar()

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        withJava()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":ai-mocks-core"))
                api(libs.ktor.serialization.kotlinx.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.server.netty)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.openai.java)
                implementation(libs.assertk)
                implementation(libs.assertj.core)
                implementation(libs.awaitility.kotlin)
                runtimeOnly(libs.slf4j.simple)
                implementation(libs.langchain4j.openai)
                implementation(libs.langchain4j.kotlin)
                implementation(libs.junit.jupiter.params)
            }
        }
    }
}
