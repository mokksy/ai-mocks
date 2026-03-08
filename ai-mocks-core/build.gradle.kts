plugins {
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
    alias(libs.plugins.kover) apply true
    kotlin("plugin.serialization") apply true
}

dependencies {
    dokka(libs.mokksy)
}

dokka {
    dokkaSourceSets.configureEach {
    }
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.schema.json)
                api(libs.mokksy)
                api(project.dependencies.platform(libs.ktor.bom))
                api(libs.kotlinLogging)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":test-utils"))
            }

            languageSettings.enableLanguageFeature("MultiDollarInterpolation")
        }
    }
}
