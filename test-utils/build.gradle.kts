@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    `kotlin-convention`
    `dokka-convention`
    alias(libs.plugins.kover) apply true
}

kotlin {
    macosArm64()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
                api(libs.kotlinx.coroutines.test)
                api(libs.kotlinx.serialization.json)
            }
        }
    }
}
