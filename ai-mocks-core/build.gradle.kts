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
                api(libs.kotlinx.serialization.json)
                api(project(":mokksy"))
                api(project.dependencies.platform(libs.ktor.bom))
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
