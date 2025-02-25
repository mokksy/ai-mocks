plugins {
    base
//    `maven-publish`
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
//    apply(plugin = "org.jetbrains.kotlin.multiplatform")
//    apply(plugin = "maven-publish")
//    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.dokka-javadoc")
    apply(plugin = "com.diffplug.spotless")

    tasks.register<Jar>("dokkaJavadocJar", Jar::class) {
        dependsOn(tasks.dokkaGenerate)
        from("build/dokka/javadoc")
        archiveClassifier.set("javadoc")
    }

//    publishing {
//        publications {
//            create<MavenPublication>("maven") {
//                from(components["kotlin"])
//                artifact(tasks["dokkaJavadocJar"])
//
//                // Use project-level name
//                artifactId = project.name
//
//                pom {
//                    name = project.name
//                    description = project.description
//                    url = "https://github.com/kpavlov/ai-mocks"
//                    licenses {
//                        license {
//                            name = "MIT License"
//                            url = "https://opensource.org/licenses/MIT"
//                        }
//                    }
//                    developers {
//                        developer {
//                            id = "kpavlov"
//                            name = "Konstantin Pavlov"
//                            url = "https://github.com/kpavlov"
//                        }
//                    }
//                    scm {
//                        connection = "scm:git:git://github.com/kpavlov/ai-mocks.git"
//                        developerConnection = "scm:git:ssh://github.com/kpavlov/ai-mocks.git"
//                        url = "https://github.com/kpavlov/ai-mocks"
//                    }
//                }
//            }
//        }
//    }

//    signing {
//        sign(publishing.publications["maven"])
//    }

//    spotless {
//        kotlin {
//            ktlint()
//        }
//    }
}

dependencies {
    kover(project(":mokksy"))
    kover(project(":ai-mocks-core"))
    kover(project(":ai-mocks-openai"))
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
