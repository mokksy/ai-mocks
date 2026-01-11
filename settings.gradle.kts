rootProject.name = "ai-mocks"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":a2a-client",
    ":ai-mocks-a2a",
    ":ai-mocks-a2a-models",
    ":ai-mocks-anthropic",
    ":ai-mocks-core",
    ":ai-mocks-gemini",
    ":ai-mocks-ollama",
    ":ai-mocks-openai",
    ":test-utils",
    ":docs",
    ":mokksy",
)
