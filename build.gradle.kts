plugins {
    base
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.kover)
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
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.dokka-javadoc")
    apply(plugin = "com.diffplug.spotless")

    tasks.register<Jar>("dokkaJavadocJar", Jar::class) {
        dependsOn(tasks.dokkaGenerate)
        from("build/dokka/javadoc")
        archiveClassifier.set("javadoc")
    }
}

dependencies {
    kover(project(":a2a-client"))
    kover(project(":ai-mocks-a2a"))
    kover(project(":ai-mocks-a2a-models"))
    kover(project(":ai-mocks-anthropic"))
    kover(project(":ai-mocks-core"))
    kover(project(":ai-mocks-openai"))
    kover(project(":mokksy"))
}

kover {
    reports {

        total {
            xml
            html
        }

        verify {
            rule {
                bound {
                    minValue = 70
                }
            }
        }
    }
}

tasks.dokkaHtmlMultiModule {
    moduleName.set("AI-Mocks")
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
