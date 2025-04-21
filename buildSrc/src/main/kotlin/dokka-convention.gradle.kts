import org.jetbrains.dokka.gradle.DokkaTask

/**
 * Common conventions for generating documentation with Dokka.
 */

plugins {
    id("org.jetbrains.dokka")
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        externalDocumentationLink {
            url.set(uri("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
            packageListUrl.set(
                rootProject.projectDir.resolve("serialization.package.list").toURI().toURL()
            )
        }
        sourceRoots.from(
            file("src/commonMain/kotlin"),
            file("src/jvmMain/kotlin")
        )
    }

}

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            // Read docs for more details: https://kotlinlang.org/docs/dokka-gradle.html#source-link-configuration
            remoteUrl("https://github.com/mokksy/ai-mocks/tree/master")
            localDirectory.set(rootDir)
        }
    }
}
