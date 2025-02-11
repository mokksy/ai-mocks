pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "ai-mocks"

include(":mokksy", ":ai-mocks-core", ":ai-mocks-openai")
