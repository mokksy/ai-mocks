import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
//    id("org.jetbrains.dokka")
//    id("org.jetbrains.dokka-javadoc")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    explicitApi()

    withSourcesJar(publish = true)

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        withJava()
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
        allWarningsAsErrors = true
        progressiveMode = true
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs =
            listOf(
                "-Xjvm-default=all",
                // Suppress deprecation warnings because we may still reference and test deprecated members.
//                "-Xsuppress-warning=DEPRECATION",
            )
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// Run tests in parallel to some degree.
tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    forkEvery = 100
}
