import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/
configure<MavenPublishBaseExtension> {
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

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
        name.set(project.name)
        description.set(project.description)
        url.set("https://github.com/mokksy/ai-mocks")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id = "kpavlov"
                name = "Konstantin Pavlov"
                url = "https://github.com/kpavlov"
            }
        }

        scm {
            connection.set("scm:git:git://github.com/mokksy/ai-mocks.git")
            developerConnection.set("scm:git:ssh://github.com/mokksy/ai-mocks.git")
            url.set("https://github.com/mokksy/ai-mocks")
        }
    }
}
