plugins {
    base
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    `dokka-convention`
    alias(libs.plugins.nexusPublish) // https://github.com/gradle-nexus/publish-plugin
    alias(libs.plugins.openrewrite)
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    signing
}

allprojects {
    repositories {
        mavenCentral()
    }
}

// Common configuration for subprojects
subprojects {
    apply {
        plugin("org.jetbrains.dokka")
        plugin("org.jetbrains.dokka-javadoc")
        plugin("com.diffplug.spotless")
        plugin("dev.detekt")
    }

    detekt {
        config = files("$rootDir/detekt.yml")
        buildUponDefaultConfig = true
    }
}

dependencies {
    kover(project(":a2a-client"))
    kover(project(":test-utils"))
    kover(project(":ai-mocks-a2a"))
    kover(project(":ai-mocks-a2a-models"))
    kover(project(":ai-mocks-anthropic"))
    kover(project(":ai-mocks-core"))
    kover(project(":ai-mocks-gemini"))
    kover(project(":ai-mocks-ollama"))
    kover(project(":ai-mocks-openai"))
    kover(project(":mokksy"))
}

kover {
    reports {
        filters {
            includes {
                classes("dev.mokksy.*")
            }
        }

        total {
            xml
            html
        }

        verify {
            rule {
                bound {
                    minValue = 65
                }
            }
        }
    }
}

rewrite {
    activeRecipe(
//        "org.openrewrite.kotlin.format.AutoFormat",
        "org.openrewrite.gradle.MigrateToGradle8",
        "org.openrewrite.gradle.RemoveRedundantDependencyVersions",
        "org.openrewrite.kotlin.cleanup.RemoveLambdaArgumentParentheses",
        "org.openrewrite.kotlin.cleanup.UnnecessaryTypeParentheses",
    )
    isExportDatatables = true
}
