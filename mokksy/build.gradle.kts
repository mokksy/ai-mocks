plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kover) apply true
}

group = "me.kpavlov.mokksy"

tasks.withType<Test> {
    useJUnitPlatform()
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
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
                api(libs.ktor.server.core)
                implementation(libs.ktor.server.double.receive)
                implementation(libs.ktor.server.sse)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockito.kotlin)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
            withSourcesJar()
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.call.logging)
            }
            withSourcesJar()
        }
        val jvmTest by getting {
            dependencies {
//                implementation(libs.slf4j.simple)
                implementation(libs.ktor.client.java)

                implementation(libs.junit.jupiter.engine)
                implementation(libs.junit.jupiter.params)
            }
        }
    }
}
