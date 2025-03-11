plugins {
    kotlin("jvm") apply false
    `dokka-convention`
}

dependencies {
    dokka(project(":mockksy"))
    dokka(project(":ai-mocks-core"))
    dokka(project(":ai-mocks-openai"))
}

dokka {
    moduleName.set("AI-Mocks")

    dokkaPublications.html {
        includes.from("../README.md")
    }
}
