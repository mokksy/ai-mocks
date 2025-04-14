plugins {
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
    `kotlin-convention`
    `publish-convention`
}

/*
buildscript {
    dependencies {
        classpath("net.pwall.json:json-kotlin-gradle:0.119")
    }
}

apply<JSONSchemaCodegenPlugin>()

configure<JSONSchemaCodegen> {
    val schemaUrl =
        "https://raw.githubusercontent.com/google/A2A/refs/heads/main/specification/json/a2a.json"
    packageName.set("me.kpavlov.aimocks.a2a.model")
    generatorComment.set("This was generated from A2A Schema: $schemaUrl")
    inputs {
        inputCompositeURI {
            uri = uri(schemaUrl)
            pointer = "/\$defs"
            exclude.set(
                listOf(
                    "DataPart",
                    "FilePart",
                    "MethodNotFoundError",
                    "TextPart",
                    "SendTaskRequest",
                    "SendTaskResponse",
                    "TaskNotCancelableError",
                    "UnsupportedOperationError",
                    "TaskNotFoundError",
                    "SendTaskStreamingResponse",
                    "PushNotificationNotSupportedError",
                ),
            )
        }
    }
    configFile.set(layout.files("codegen-config.json").first())
}


Generate classes manually, when needed
tasks.withType<KotlinCompile> {
    dependsOn(tasks.named("generate"))
}
*/

kotlin {
    compilerOptions {
        optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
    }

    sourceSets {
        commonMain {
            /*
            val generatedDir =
                layout.buildDirectory.dir(
                    "generated-sources/kotlin",
                )
            kotlin.srcDir(generatedDir)
             */
            dependencies {
                api(libs.kotlinx.serialization.json)
                api(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
                implementation(libs.assertk)
                implementation(libs.kotlinLogging)
            }
        }
//
//        jvmMain {
//            dependencies {
//                implementation(libs.ktor.server.netty)
//                implementation(libs.ktor.serialization.jackson)
//            }
//        }
//
//        jvmTest {
//            dependencies {
//                implementation(kotlin("test"))
//                implementation(libs.assertj.core)
//                implementation(libs.awaitility.kotlin)
//                implementation(libs.junit.jupiter.params)
//                runtimeOnly(libs.slf4j.simple)
//            }
//        }
    }
}
