import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") apply true
    alias(libs.plugins.kover) apply true
    `kotlin-convention`
    `publish-convention`
    id("org.openapi.generator") version "7.12.0"
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.openApiGenerate)
}

tasks.withType<DokkaGenerateTask> {
    dependsOn(tasks.openApiGenerate)
}

tasks.named("jvmSourcesJar") {
    dependsOn(tasks.openApiGenerate)
}

openApiGenerate {
    generatorName = "kotlin"
    // https://github.com/openai/openai-openapi/blob/master/openapi.yaml
    remoteInputSpec =
        "https://raw.githubusercontent.com/openai/openai-openapi/refs/heads/master/openapi.yaml"
//        "https://raw.githubusercontent.com/openai/openai-openapi/refs/heads/update-2024-11-04/openapi.yaml"

    outputDir =
        layout.buildDirectory
            .dir("generated-sources")
            .get()
            .asFile.path

    modelPackage = "me.kpavlov.aimocks.openai.model"
    generateModelTests = false
    generateModelDocumentation = false
    cleanupOutput = true
    skipValidateSpec = true
    library = "multiplatform"
    globalProperties.set(
        mapOf(
            "models" to // generate only models
                listOf(
                    "ChatCompletionRole",
                    "ChatCompletionStreamOptions",
                    "Error",
                    "Refusal",
                    "ResponsePropertiesToolChoice",
                    "Reasoning",
                    "ReasoningEffort",
                    "OutputMessage",
                    "OutputContent",
                    "OutputText",
                    "Annotation",
                    "FileCitation",
                    "FilePath",
                    "UrlCitation",
                    "ResponsePropertiesToolChoice",
                    "InputAudio",
                    "InputContent",
                    "InputFile",
                    "InputImage",
                    "InputMessageResource",
                    "InputText",
                ).joinToString(","),
        ),
    )
    configOptions.set(
        mapOf(
            "enumPropertyNaming" to "UPPERCASE",
            "dateLibrary" to "kotlinx-datetime",
            "explicitApi" to "true",
//            "modelMutable" to "true",
        ),
    )
}

kotlin {

    sourceSets {
        commonMain {
            val generatedDir =
                layout.buildDirectory.dir(
                    "generated-sources/src/commonMain/kotlin",
                )
            kotlin.srcDir(generatedDir)

            dependencies {
                api(project(":ai-mocks-core"))
                api(libs.ktor.serialization.kotlinx.json)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinLogging)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.server.netty)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.assertj.core)
                implementation(libs.awaitility.kotlin)
                implementation(libs.finchly)
                implementation(libs.junit.jupiter.params)
                implementation(libs.langchain4j.kotlin)
                implementation(libs.langchain4j.openai)
                implementation(libs.openai.java)
                implementation(libs.spring.ai.openai)
                implementation(project.dependencies.platform(libs.spring.ai.bom))
                implementation(project.dependencies.platform(libs.spring.bom))
                runtimeOnly(libs.slf4j.simple)
            }
        }
    }
}
