plugins {
    `kotlin-convention`
    `dokka-convention`
    alias(libs.plugins.kover) apply true
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.json)
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
            }
        }
    }
}
