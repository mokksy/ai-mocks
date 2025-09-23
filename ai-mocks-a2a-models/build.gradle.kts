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
    compilerOptions {
        optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":test-utils"))
                implementation(libs.assertk)
                implementation(libs.kotlinLogging)
            }
        }
    }
}
