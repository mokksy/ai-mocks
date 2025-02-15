import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
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
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":mokksy"))
            }
        }
    }
}
