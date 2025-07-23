plugins {
    kotlin("jvm") apply false
    `dokka-convention`
}

dependencies {
    dokka(project(":mokksy"))
    dokka(project(":a2a-client"))
    dokka(project(":ai-mocks-a2a"))
    dokka(project(":ai-mocks-a2a-models"))
    dokka(project(":ai-mocks-anthropic"))
    dokka(project(":ai-mocks-core"))
    dokka(project(":ai-mocks-gemini"))
    dokka(project(":ai-mocks-ollama"))
    dokka(project(":ai-mocks-openai"))
}

dokka {
    moduleName.set("AI-Mocks")

    dokkaPublications.html {
        outputDirectory = layout.projectDirectory.dir("public/apidocs")
    }
}
