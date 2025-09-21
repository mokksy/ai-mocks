import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

tasks.register<Jar>(name = "dokkaJavadocJar") {
    group = "dokka"
    description = "Assembles a jar archive containing javadoc documentation."
    val javadocTask = tasks.named("dokkaGenerateModuleJavadoc")
    dependsOn(javadocTask)
    from("build/dokka-module/javadoc")
    archiveClassifier.set("javadoc")
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/
configure<MavenPublishBaseExtension> {
    signAllPublications()
    publishToMavenCentral()

    logger.debug(
        "{}:{}:{} - {}({})",
        project.group,
        project.name,
        version,
        project.name,
        project.description,
    )
    coordinates(project.group.toString(), project.name, project.version.toString())
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaJavadocJar"),
            sourcesJar = true,
        ),
    )

    pom {
        name = project.name
        description = project.description
        url = "https://mokksy.dev"
        inceptionYear = "2025"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "kpavlov"
                roles = setOf("author", "developer")
                name = "Konstantin Pavlov"
                url = "https://github.com/kpavlov"
            }
        }

        scm {
            connection = "scm:git:git://github.com/mokksy/ai-mocks.git"
            developerConnection = "scm:git:ssh://github.com/mokksy/ai-mocks.git"
            url = "https://github.com/mokksy/ai-mocks"
        }

        issueManagement {
            url = "https://github.com/mokksy/ai-mocks/issues"
            system = "GitHub"
        }
    }
}
