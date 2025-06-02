plugins {
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
    alias(libs.plugins.kover) apply true
    kotlin("plugin.serialization") apply true
}

dokka {
    dokkaSourceSets.configureEach {
    }
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(project(":mokksy"))
                api(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }

            languageSettings.enableLanguageFeature("MultiDollarInterpolation")
        }
    }
}
