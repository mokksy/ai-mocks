---
title: "Gemini"
#weight: 30
toc: true
---

AI-Mocks Gemini is a specialized mock server implementation for mocking the Google Vertex AI Gemini API, built using Mokksy.

`MockGemini` is tested against the Spring AI framework with the Vertex AI Gemini integration.

Currently, it supports basic content generation requests.

## Quick Start

Add Dependency Include the library in your test dependencies (Maven or Gradle).

{{< tabs "dependencies" >}}
{{< tab "Gradle" >}}
```kotlin
implementation("me.kpavlov.aimocks:ai-mocks-gemini-jvm:$latestVersion")
```
    {{< /tab >}}
    {{< tab "Maven" >}}
```xml
<dependency>
  <groupId>me.kpavlov.aimocks</groupId>
  <artifactId>ai-mocks-gemini-jvm</artifactId>
  <version>[LATEST_VERSION]</version>
</dependency>
```
    {{< /tab >}}
    {{< /tabs >}}

## Content Generation API

Set up a mock server and define mock responses:

```kotlin
val gemini = MockGemini(verbose = true)
```

Let's simulate Gemini content generation API:

```kotlin
// Define mock response
gemini.generateContent {
  temperature = 0.7
  model = "gemini-2.0-flash"
  project = "your-project-id"
  location = "us-central1"
  systemMessageContains("helpful pirate")
  userMessageContains("say 'Hello!'")
} responds {
  content = "Ahoy there, matey! Hello!"
  finishReason = "stop"
  delay = 42.milliseconds // delay before answer
}
```

## Integration with Spring-AI
          
First, we need a function to create VertexAI client, configured to use the arbitrary server endpoint and credentials.

```kotlin
internal fun createTestVertexAI(
    endpoint: String,
    projectId: String,
    location: String,
    timeout: Duration,
): VertexAI {
    try {
        val channelProvider =
            LlmUtilityServiceStubSettings
                .defaultHttpJsonTransportProviderBuilder()
                .setEndpoint(endpoint)
                .build()

        val newHttpJsonBuilder = LlmUtilityServiceStubSettings.newHttpJsonBuilder()
        newHttpJsonBuilder.unaryMethodSettingsBuilders().forEach { builder ->
            builder.setSimpleTimeoutNoRetriesDuration(timeout.toJavaDuration())
        }

        val llmUtilityServiceStubSettings =
            newHttpJsonBuilder
                .setEndpoint(endpoint)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .setTransportChannelProvider(channelProvider)
                .build()

        val llmUtilityServiceClient =
            LlmUtilityServiceClient.create(
                LlmUtilityServiceSettings.create(llmUtilityServiceStubSettings),
            )

        val predictionServiceSettingsBuilder =
            PredictionServiceSettings
                .newHttpJsonBuilder()
                .setEndpoint(endpoint)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .applyToAllUnaryMethods { updater ->
                    updater.setSimpleTimeoutNoRetriesDuration(timeout.toJavaDuration()) as? Void?
                }

        val predictionServiceSettings = predictionServiceSettingsBuilder.build()
        val predictionClient = PredictionServiceClient.create(predictionServiceSettings)

        return VertexAI
            .Builder()
            .setTransport(Transport.REST)
            .setProjectId(projectId)
            .setLocation(location)
            .setLlmClientSupplier { llmUtilityServiceClient }
            .setPredictionClientSupplier { predictionClient }
            .setCredentials(ApiKeyCredentials.create("dummy-key"))
            .build()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
```

Then we should create `MockGemini` server and test Spring-AI integration:

```kotlin
// create mock server
val gemini = MockGemini(verbose = true)

// Create a VertexAI client that connects to the mock server
val vertexAI = createTestVertexAI(
    endpoint = gemini.baseUrl(),
    projectId = "your-project-id",
    location = "us-central1",
    timeout = 5.seconds,
)

// create Spring-AI client
val chatClient =
  ChatClient
    .builder(
      VertexAiGeminiChatModel
        .builder()
        .vertexAI(vertexAI)
        .build(),
    ).build()

// Set up a mock for the LLM call
gemini.generateContent {
  temperature = 0.7
  model = "gemini-2.0-flash"
  project = "your-project-id"
  location = "us-central1"
  systemMessageContains("You are a helpful pirate")
  userMessageContains("Just say 'Hello!'")
} responds {
  content = "Ahoy there, matey! Hello!"
  finishReason = "stop"
  delay = 42.milliseconds
}

// Configure Spring-AI client call
val response =
  chatClient
    .prompt()
    .system("You are a helpful pirate")
    .user("Just say 'Hello!'")
    .options(ChatOptions.builder().temperature(0.7).build())
    // Make a call
    .call()
    .chatResponse()

// Verify the response
response shouldNotBeNull {
  result shouldNotBeNull {
    metadata.finishReason shouldBe "STOP"
    output.text shouldBe "Ahoy there, matey! Hello!"
  }
}
```

Check for examples in the [integration tests](https://github.com/mokksy/ai-mocks/tree/main/ai-mocks-gemini/src/jvmTest/kotlin/me/kpavlov/aimocks/gemini/springai).
