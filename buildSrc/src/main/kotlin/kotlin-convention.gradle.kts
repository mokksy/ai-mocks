@file:OptIn(ExperimentalWasmDsl::class)

import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0

plugins {
    kotlin("multiplatform")
}

kotlin {

    compilerOptions {
        languageVersion = KOTLIN_2_0
        apiVersion = KOTLIN_2_0
        allWarningsAsErrors = true
        extraWarnings = true
        freeCompilerArgs =
            listOf(
                "-Wextra",
                "-Xjvm-default=all",
                "-Xmulti-dollar-interpolation",
            )
    }
    coreLibrariesVersion = "2.0.10"

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    explicitApi()

    withSourcesJar(publish = true)

    jvm {
        compilerOptions {
            javaParameters = true
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

configure<SpotlessExtension> {
    ratchetFrom("origin/main")

    kotlinGradle {
        ktlint()
    }

    kotlin {
        ktfmt().kotlinlangStyle()
        toggleOffOn()
    }

    format("misc") {
        target("*.md", ".gitignore")
        trimTrailingWhitespace()
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
