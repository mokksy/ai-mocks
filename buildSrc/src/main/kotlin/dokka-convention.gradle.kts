import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

/*
 * Dokka convention plugin for documentation generation.
 */
plugins {
    id("org.jetbrains.dokka")
}

dokka {
    dokkaSourceSets.configureEach {
        val moduleMd = projectDir.resolve("Module.md")
        if (moduleMd.exists()) {
            includes.from(moduleMd)
        }

        documentedVisibilities(VisibilityModifier.Public)

        sourceLink {
            localDirectory = projectDir.resolve("src")
            remoteUrl("https://github.com/mokksy/ai-mocks/tree/main/${project.name}/src")
            remoteLineSuffix = "#L"
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
            register("kotlinx-schema") {
                url("https://kotlin.github.io/kotlinx-schema/")
            }
        }
    }

    pluginsConfiguration.html {
        footerMessage = "Copyright © 2025-2026 Konstantin Pavlov"
    }
}
