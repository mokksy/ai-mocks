import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    explicitApi()

    withSourcesJar(publish = true)

    jvm {
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

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        languageVersion = KOTLIN_2_0
        apiVersion = KOTLIN_2_0
        freeCompilerArgs =
            listOf(
                "-Wextra",
                "-Xjvm-default=all",
                "-Xmulti-dollar-interpolation" ,
            )
    }
}

// Run tests in parallel to some degree.
tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    forkEvery = 100
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
    systemProperty("kotest.output.ansi", "true")
    reports {
        junitXml.required.set(true)
        junitXml.includeSystemOutLog.set(true)
        junitXml.includeSystemErrLog.set(true)
    }
}
