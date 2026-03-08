import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    base
    kotlin("jvm")
    `dokka-convention`
    alias(libs.plugins.knit)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

sourceSets {
    test {
        kotlin {
            srcDir("build/generated/knit/test/kotlin")
        }
    }
}

dependencies {
    dokka(project(":a2a-client"))
    dokka(project(":ai-mocks-a2a"))
    dokka(project(":ai-mocks-a2a-models"))
    dokka(project(":ai-mocks-anthropic"))
    dokka(project(":ai-mocks-core"))
    dokka(project(":ai-mocks-gemini"))
    dokka(project(":ai-mocks-ollama"))
    dokka(project(":ai-mocks-openai"))

    testImplementation(project(":a2a-client"))
    testImplementation(project(":ai-mocks-a2a"))
    testImplementation(project(":ai-mocks-anthropic"))
    testImplementation(project(":ai-mocks-gemini"))
    testImplementation(project(":ai-mocks-ollama"))
    testImplementation(project(":ai-mocks-openai"))
    testImplementation(libs.mokksy)

    testImplementation(platform(libs.ktor.bom))
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.java)
    testImplementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.ktor.sse)

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.coroutines.core.jvm)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.assertj.core)

    implementation(libs.anthropic.java)
    implementation(libs.google.genai)
    implementation(libs.openai.java)

    testImplementation(platform(libs.langchain4j.bom))
    testImplementation(libs.langchain4j.anthropic)
    testImplementation(libs.langchain4j.gemini)
    testImplementation(libs.langchain4j.kotlin)
    testImplementation(libs.langchain4j.ollama)
    testImplementation(libs.langchain4j.openai)

    testImplementation(platform(libs.spring.ai.bom))
    testImplementation(platform(libs.spring.bom))
    testImplementation(libs.spring.ai.client.chat)
    testImplementation(libs.spring.ai.gemini)
    testImplementation(libs.spring.ai.ollama)
    testImplementation(libs.spring.ai.openai)

    testImplementation(libs.kotlinLogging)
    testRuntimeOnly(libs.slf4j.simple)
}

tasks.test {
    useJUnitPlatform()
}

// Generated knit sources must exist before test compilation.
tasks.named("compileTestKotlin").configure {
    dependsOn(tasks.named("knit"))
}

// Decouple knit tasks from the standard build lifecycle.
// Run on demand: ./gradlew :docs:knit  or  ./gradlew :docs:knitCheck
// afterEvaluate is required here because the knit plugin wires knitCheck -> check during its own afterEvaluate.
afterEvaluate {
    tasks.named("check").configure {
        setDependsOn(
            dependsOn.filterNot { dep ->
                (dep is TaskProvider<*> && dep.name == "knitCheck") ||
                    (dep is Task && dep.name == "knitCheck") ||
                    (dep is String && dep == "knitCheck")
            },
        )
    }
}

knit {
    rootDir = project.rootDir
    files =
        fileTree(project.rootDir) {
            include("README.md")
            include("docs/content/**/*.md")
            exclude("**/build/**")
        }
    siteRoot = "https://mokksy.dev/"
}

dokka {
    moduleName.set("AI-Mocks")

    pluginsConfiguration.html {
        footerMessage = "Copyright © 2025-2026 Konstantin Pavlov"
    }

    dokkaPublications.html {
        outputDirectory = layout.projectDirectory.dir("public/apidocs")
    }
}
