/**
 * Common conventions for generating documentation with Dokka.
 */

plugins {
    id("org.jetbrains.dokka")
}

dokka {
    dokkaSourceSets.configureEach {
        // includes.from("Module.md")

        // Suppress certain warnings
        suppressObviousFunctions.set(false)

        sourceLink {
            // Read docs for more details: https://kotlinlang.org/docs/dokka-gradle.html#source-link-configuration
            remoteUrl("https://github.com/mokksy/ai-mocks/tree/master")
            localDirectory.set(rootDir)
        }

        // Add classpath to help Dokka resolve external types and project dependencies
        // This includes both external libraries and inter-module dependencies
        // Only add if Kotlin Multiplatform plugin is applied
        pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            val jvmCompileClasspath = configurations.findByName("jvmCompileClasspath")
            if (jvmCompileClasspath != null) {
                classpath.from(jvmCompileClasspath)
            }
        }

        externalDocumentationLinks {
            register("ktor") {
                url("https://api.ktor.io/")
            }

            register("kotlinx-coroutines") {
                url("https://kotlinlang.org/api/kotlinx.coroutines/")
            }

            register("kotlinx-serialization") {
                url("https://kotlinlang.org/api/kotlinx.serialization/")
            }
        }
    }
}
