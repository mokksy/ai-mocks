@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1

plugins {
    kotlin("multiplatform")
}

kotlin {

    compilerOptions {
        languageVersion = KOTLIN_2_1
        apiVersion = KOTLIN_2_1
        allWarningsAsErrors = true
        extraWarnings = true
        freeCompilerArgs =
            listOf(
                "-Wextra",
                "-Xmulti-dollar-interpolation",
            )
    }
    coreLibrariesVersion = "2.1.21"

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
tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    forkEvery = 100
    testLogging {
        showStandardStreams = true
        events("failed")
    }
    systemProperty("kotest.output.ansi", "true")
    reports {
        junitXml.required.set(true)
        junitXml.includeSystemOutLog.set(true)
        junitXml.includeSystemErrLog.set(true)
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
