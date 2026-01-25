import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateModuleTask

/**
 * Publishing convention for Kotlin Multiplatform modules.
 * Configures Maven Central publishing with HTML docs as javadoc.jar.
 */
plugins {
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

/**
 * Task that provides HTML documentation directory for javadoc jar creation.
 * Dokka Javadoc doesn't support KMP, so HTML format is used.
 * Maven Central accepts HTML docs in the javadoc JAR.
 *
 * This is a Sync task that copies Dokka output to a staging directory,
 * which Vanniktech uses to create the javadoc jar.
 */
val dokkaHtmlForJavadoc by tasks.registering(Sync::class) {
    group = "dokka"
    description = "Prepares Dokka HTML output for javadoc jar."
    from(
        tasks
            .named<DokkaGenerateModuleTask>("dokkaGenerateModuleHtml")
            .flatMap { it.outputDirectory },
    )
    into(layout.buildDirectory.dir("dokka-javadoc"))
}

mavenPublishing {
    signAllPublications()
    publishToMavenCentral()

    coordinates(project.group.toString(), project.name, project.version.toString())

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtmlForJavadoc"),
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
