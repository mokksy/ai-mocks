plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kover) apply true
}

kotlin {

    jvmToolchain(17)

    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":mokksy"))
            }
            withSourcesJar()
        }
    }
}
