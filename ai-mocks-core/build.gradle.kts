import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kover) apply true
}

description =
    "AI-Mocks Core is a Kotlin Multiplatform library that provides core abstractions for Mocking LLMs"

kotlin {

    jvmToolchain(17)

    explicitApi()
    withSourcesJar()

    dokka {
        dokkaPublications.html
        dokkaPublications.javadoc
    }

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":mokksy"))
            }
        }
    }
}
