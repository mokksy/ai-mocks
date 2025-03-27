plugins {
    `kotlin-convention`
    `publish-convention`
    alias(libs.plugins.kover) apply true
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                api(project(":mokksy"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
