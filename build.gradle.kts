plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    kotlin("multiplatform") version "2.1.10" apply false
    kotlin("plugin.serialization") version "2.1.10" apply false

    `maven-publish`
    signing
    alias(libs.plugins.nexusPublish) // https://github.com/gradle-nexus/publish-plugin
}

allprojects {
    group = "me.kpavlov.aimocks" // Replace with your groupId
    version = "0.1.2-SNAPSHOT" // Replace as needed

    repositories {
        mavenCentral()
    }
}

// Common configuration for subprojects
subprojects {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")

//    kotlin {
//        // Add JVM as the primary target for all modules
//        jvm {
//            compilations.all {
//                kotlinOptions.jvmTarget = "17" // Or 17 as needed
//            }
//            withJava()
//        }
//
//        sourceSets {
//            val commonMain by getting {
//                dependencies {
//                    implementation(kotlin("stdlib"))
//                }
//            }
//            val commonTest by getting {
//                dependencies {
//                    implementation(kotlin("test"))
//                }
//            }
//        }
//    }

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
}

dependencies {
    kover(project(":mokksy"))
    kover(project(":ai-mocks-core"))
    kover(project(":ai-mocks-openai"))
}

kover {
    reports {
        filters {
//        excludes.classes("kotlinx.kover.examples.merged.utils.*", "kotlinx.kover.examples.merged.subproject.utils.*")
//        includes.classes("kotlinx.kover.examples.merged.*")
        }

        total {
            xml
        }

        verify {
            /*
            rule {
                bound {
                    minValue.set(50)
                    maxValue.set(75)
                }
            }
             */
        }
    }
}
