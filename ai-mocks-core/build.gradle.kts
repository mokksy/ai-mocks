plugins {
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
    alias(libs.plugins.kover) apply true
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
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
