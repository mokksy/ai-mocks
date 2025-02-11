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
                api(project(":ai-mocks-core"))
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.server.netty)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.openai.java)
                implementation(libs.awaitility.kotlin)
//                implementation(libs.ktor.client.java)
                implementation(libs.langchain4j.openai)
                implementation(libs.langchain4j.kotlin)
                implementation(libs.junit.jupiter.params)
            }
        }
    }
}
