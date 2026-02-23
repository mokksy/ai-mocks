@file:OptIn(
    ExperimentalWasmDsl::class,
    ExperimentalAbiValidation::class,
)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    kotlin("multiplatform")
}

kotlin {

    abiValidation {
        enabled = true
    }

    compilerOptions {
        languageVersion = KOTLIN_2_2
        apiVersion = KOTLIN_2_2
        allWarningsAsErrors = true
        extraWarnings = true
        freeCompilerArgs =
            listOf(
                "-Wextra",
                "-Xmulti-dollar-interpolation",
            )
        optIn.add("kotlin.time.ExperimentalTime")
    }
    coreLibrariesVersion = "2.2.21"

    jvmToolchain(17)

    explicitApi()

    withSourcesJar(publish = true)

    jvm {
        compilerOptions {
            javaParameters = true
            jvmDefault.set(JvmDefaultMode.ENABLE)
            jvmTarget = JvmTarget.JVM_17
            // Enable debug symbols and line number information
            freeCompilerArgs.addAll(
                "-Xdebug",
            )
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
}

// Run tests in parallel to some degree.
private val defaultForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
tasks.withType<Test>().configureEach {
    maxParallelForks =
        providers
            .gradleProperty("test.maxParallelForks")
            .map {
                it.toIntOrNull() ?: defaultForks
            }.getOrElse(defaultForks)

    forkEvery = 100
    testLogging {
        showStandardStreams = true
        events("failed")
    }

    systemProperty("kotest.output.ansi", "true")
    reports {
        junitXml.required = true
        junitXml.includeSystemOutLog = true
        junitXml.includeSystemErrLog = true
    }
}

tasks.named("detekt").configure {
    dependsOn(
        "detektCommonMainSourceSet",
        "detektMainJvm",
        "detektCommonTestSourceSet",
        "detektTestJvm",
    )
}
