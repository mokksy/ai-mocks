import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    `maven-publish`
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.nexusPublish) // https://github.com/gradle-nexus/publish-plugin
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    id("org.openrewrite.rewrite") version "7.1.5"
    signing
    id("com.diffplug.spotless") version "7.0.2"
}

allprojects {
    group = "me.kpavlov.aimocks" // Replace with your groupId
    version = "0.1.2-SNAPSHOT" // Replace as needed

    repositories {
        mavenCentral()
    }
}

tasks {
    withType<Jar> {
        archiveClassifier.set("sources")
    }
}

// Common configuration for subprojects
subprojects {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.diffplug.spotless")

    // configure all format tasks at once
    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            includes.from("README.md")
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["kotlin"])

                // Use project-level name
                artifactId = project.name

                pom {
                    name.set("AI Mocks - ${project.name.capitalize()}")
                    description.set("Description for ${project.name}")
                    url.set("https://github.com/kpavlov/ai-mocks")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("kpavlov")
                            name.set("Konstantin Pavlov")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/kpavlov/ai-mocks.git")
                        developerConnection.set("scm:git:ssh://github.com/kpavlov/ai-mocks.git")
                        url.set("https://github.com/kpavlov/ai-mocks/tree/main")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "SonatypeOSSRH"
                url =
                    if (version.toString().endsWith("SNAPSHOT")) {
                        uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    } else {
                        uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    }

                credentials {
                    username = project.findProperty("ossrhUsername") as String?
                    password = project.findProperty("ossrhPassword") as String?
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            project.findProperty("signing.keyId") as String?,
            project.findProperty("signing.secretKey") as String?,
            project.findProperty("signing.password") as String?,
        )
        sign(publishing.publications["maven"])
    }

    spotless {
        kotlin {
            ktlint()
        }
    }
}

dependencies {
    kover(project(":mokksy"))
    kover(project(":ai-mocks-core"))
    kover(project(":ai-mocks-openai"))
}

spotless {
    ratchetFrom("origin/main")

    kotlinGradle {
        ktlint()
    }
    format("misc") {
        target("*.md", ".gitignore")
        trimTrailingWhitespace()
    }
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
    setExportDatatables(true)
}
