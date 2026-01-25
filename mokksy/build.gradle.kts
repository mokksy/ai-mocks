plugins {
    kotlin("plugin.serialization") apply true
    `kotlin-convention`
    `dokka-convention`
    `publish-convention`
    `netty-convention`
    `shadow-convention`
    alias(libs.plugins.kotlinx.atomicfu) apply true
    alias(libs.plugins.kover) apply true
}

dokka {
    dokkaSourceSets.configureEach {
        // includes.from("README.md")
    }
}

kotlin {

    macosArm64 {
        binaries {
            framework()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
                api(libs.ktor.server.content.negotiation)
                api(libs.ktor.server.core)
                api(project.dependencies.platform(libs.ktor.bom))
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.server.double.receive)
                implementation(libs.ktor.server.sse)
                implementation(libs.kotlinLogging)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk)
                implementation(libs.kotlinLogging)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.server.test.host)
            }
        }

        jvmMain {
            dependencies {
                implementation(project.dependencies.platform(libs.netty.bom))
                implementation(libs.jansi)
                implementation(libs.ktor.server.call.logging)
                implementation(libs.ktor.server.netty)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.datafaker)
                implementation(libs.junit.jupiter.params)
                implementation(libs.ktor.client.java)
                implementation(libs.ktor.serialization.jackson)
                implementation(libs.mockk)
                implementation(libs.mockk.dsl)
                runtimeOnly(libs.slf4j.simple)
            }
        }

        nativeMain {
            dependencies {
                implementation(libs.ktor.server.cio)
            }
        }

        nativeTest {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifactId = "${project.name}-standalone"
            artifact(tasks.named("shadowJar")) {
                classifier = ""
                extension = "jar"
            }
            artifact(tasks["jvmSourcesJar"]) {
                classifier = "sources"
            }
        }
    }
}
