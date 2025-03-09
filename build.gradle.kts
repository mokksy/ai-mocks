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
    kover(project(":mokksy"))
    kover(project(":ai-mocks-core"))
    kover(project(":ai-mocks-openai"))
    kover(project(":ai-mocks-anthropic"))
}

kover {
    reports {

        total {
            xml
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

tasks.dokkaHtmlMultiModule {
    moduleName.set("AI-Mocks")
}

rewrite {
    activeRecipe(
//        "org.openrewrite.kotlin.format.AutoFormat",
        "org.openrewrite.gradle.MigrateToGradle8",
        "org.openrewrite.gradle.RemoveRedundantDependencyVersions",
    )
    isExportDatatables = true
}
