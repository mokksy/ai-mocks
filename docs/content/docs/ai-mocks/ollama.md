---
title: "Ollama"
#weight: 30
toc: true
---

AI-Mocks Ollama is a specialized mock server implementation for mocking
the [Ollama API](https://github.com/ollama/ollama/blob/main/docs/api.md), built using Mokksy.

`MockOllama` is tested against the [LangChain4j](https://github.com/langchain4j/langchain4j) framework with the Ollama
integration.

Currently, it supports the main endpoints of the Ollama API, including:

- Generate completions
- Chat completions
- Model management
- Embeddings

## Quick Start

Add Dependency Include the library in your test dependencies (Maven or Gradle).

{{< tabs "dependencies" >}}
{{< tab "Gradle" >}}
```kotlin
implementation("dev.mokksy.aimocks:ai-mocks-ollama-jvm:$latestVersion")
```

{{< /tab >}}
{{< tab "Maven" >}}
```xml
<dependency>
  <groupId>dev.mokksy.aimocks</groupId>
  <artifactId>ai-mocks-ollama-jvm</artifactId>
  <version>[LATEST_VERSION]</version>
</dependency>
```

{{< /tab >}}
{{< /tabs >}}

## Basic Setup

Set up a mock server and define mock responses:

```kotlin
// Create a mock Ollama server
val ollama = MockOllama(verbose = true)

// Get the base URL of the mock server
val baseUrl = ollama.baseUrl()
```

## Generate Completions API

Let's simulate Ollama's Generate Completions API:

```kotlin
// Define mock response
ollama.generate {
  model = "llama3"
  userMessageContains("Tell me a joke")
} responds {
  content("Why did the chicken cross the road? To get to the other side!")
  doneReason("stop")
  delay = 42.milliseconds
}

// Create request
val request = GenerateRequest(
  model = "llama3",
  prompt = "Tell me a joke",
  stream = false,
  options = mapOf(
    "temperature" to "0.7",
    "top_p" to "0.9"
  )
)

// Send request to mock server
val httpRequest = HttpRequest.newBuilder()
  .uri(URI.create("${ollama.baseUrl()}/api/generate"))
  .header("Content-Type", "application/json")
  .POST(
    HttpRequest.BodyPublishers.ofString(
      json.encodeToString(GenerateRequest.serializer(), request)
    )
  )
  .build()

val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

// Verify response
response.statusCode() shouldBe 200
val generateResponse = json.decodeFromString<GenerateResponse>(response.body())
generateResponse.response shouldBe "Why did the chicken cross the road? To get to the other side!"
generateResponse.model shouldBe "llama3"
generateResponse.done shouldBe true
generateResponse.doneReason shouldBe "stop"
```

## Chat Completions API

Let's simulate Ollama's Chat Completions API:

```kotlin
// Define mock response
ollama.chat {
  model = "llama3"
  userMessageContains("Hello")
} responds {
  content("Hello, how can I help you today?")
  delay = 42.milliseconds
}

// Create request
val request = ChatRequest(
  model = "llama3",
  messages = listOf(
    Message(
      role = "user",
      content = "Hello"
    )
  ),
  stream = false,
  options = JsonObject(
    mapOf(
      "temperature" to JsonPrimitive("0.7"),
      "top_p" to JsonPrimitive("0.9")
    )
  )
)

// Send request to mock server
val httpRequest = HttpRequest.newBuilder()
  .uri(URI.create("${ollama.baseUrl()}/api/chat"))
  .header("Content-Type", "application/json")
  .POST(
    HttpRequest.BodyPublishers.ofString(
      json.encodeToString(ChatRequest.serializer(), request)
    )
  )
  .build()

val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

// Verify response
response.statusCode() shouldBe 200
val chatResponse = json.decodeFromString<ChatResponse>(response.body())
chatResponse.message.content shouldBe "Hello, how can I help you today?"
chatResponse.model shouldBe "llama3"
chatResponse.done shouldBe true
```

## Embeddings API

Let's simulate Ollama's Embeddings API:

```kotlin
// Define mock response for a single string input
val embeddings = listOf(listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f))

ollama.embed {
  model = "llama3"
  stringInput = "The sky is blue"
} responds {
  embeddings(embeddings)
  delay = 42.milliseconds
}

// Create request
val request = StringEmbeddingsRequest(
  model = "llama3",
  input = "The sky is blue",
  options = mapOf(
    "temperature" to "0.7",
    "top_p" to "0.9"
  )
)

// Send request to mock server
val httpRequest = HttpRequest.newBuilder()
  .uri(URI.create("${ollama.baseUrl()}/api/embed"))
  .header("Content-Type", "application/json")
  .POST(
    HttpRequest.BodyPublishers.ofString(
      json.encodeToString(StringEmbeddingsRequest.serializer(), request)
    )
  )
  .build()

val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

// Verify response
response.statusCode() shouldBe 200
val embedResponse = json.decodeFromString<EmbeddingsResponse>(response.body())
embedResponse.embeddings shouldBe embeddings
embedResponse.model shouldBe "llama3"
```

You can also mock embeddings for a list of strings:

```kotlin
// Define mock response for multiple string inputs
val embeddings = listOf(
  listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f),
  listOf(0.6f, 0.7f, 0.8f, 0.9f, 1.0f)
)

ollama.embed {
  model = "llama3"
  stringListInput = listOf("The sky is blue", "The grass is green")
} responds {
  embeddings(embeddings)
  delay = 42.milliseconds
}

// Create request
val request = StringListEmbeddingsRequest(
  model = "llama3",
  input = listOf("The sky is blue", "The grass is green"),
  options = mapOf(
    "temperature" to "0.7",
    "top_p" to "0.9"
  )
)

// Send request to mock server
val httpRequest = HttpRequest.newBuilder()
  .uri(URI.create("${ollama.baseUrl()}/api/embed"))
  .header("Content-Type", "application/json")
  .POST(
    HttpRequest.BodyPublishers.ofString(
      json.encodeToString(StringListEmbeddingsRequest.serializer(), request)
    )
  )
  .build()

val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

// Verify response
response.statusCode() shouldBe 200
val embedResponse = json.decodeFromString<EmbeddingsResponse>(response.body())
embedResponse.embeddings shouldBe embeddings
embedResponse.model shouldBe "llama3"
```

## Streaming Responses

AI-Mocks-Ollama supports streaming responses for both generate and chat endpoints:

```kotlin
// Define streaming mock response for generate endpoint
ollama.generate {
  model = "llama3"
  stream = true
  userMessageContains("Tell me a story")
} respondsStream {
  responseChunks = listOf(
    "Once upon a time",
    " in a land far, far away",
    " there lived a programmer",
    " who never had to debug in production."
  )
  delayBetweenChunks = 100.milliseconds
}

// Define streaming mock response for chat endpoint
ollama.chat {
  model = "llama3"
  stream = true
} respondsStream {
  responseChunks = listOf(
    "Hello",
    ", how can I",
    " help you today?"
  )
  delayBetweenChunks = 100.milliseconds
}
```

## Request Configuration Options

The following tables list the available configuration options for mocking Ollama API calls.

### Generate Request Configuration Options

| Option              | Description                                |
|---------------------|--------------------------------------------|
| `model`             | The model to match in the request          |
| `prompt`            | The prompt to match in the request         |
| `system`            | The system message to match in the request |
| `template`          | The template to match in the request       |
| `stream`            | Whether to match streaming requests        |
| `requestBodyString` | Adds a string matcher for the request body |

### Chat Request Configuration Options

| Option              | Description                                   |
|---------------------|-----------------------------------------------|
| `model`             | The model to match in the request             |
| `messages`          | The messages to match in the request          |
| `stream`            | Whether to match streaming requests           |
| `requestBodyString` | Adds a string matcher for the request body    |
| `userMessage`       | Adds a user message to match in the request   |
| `systemMessage`     | Adds a system message to match in the request |

### Embed Request Configuration Options

| Option              | Description                                                |
|---------------------|------------------------------------------------------------|
| `model`             | The model to match in the request                          |
| `stringInput`       | The string input to match in the request                   |
| `stringListInput`   | The list of string inputs to match in the request          |
| `truncate`          | Whether to truncate the input to fit within context length |
| `options`           | Additional model parameters to match in the request        |
| `keepAlive`         | Controls how long the model will stay loaded into memory   |
| `requestBodyString` | Adds a string matcher for the request body                 |

## Response Configuration Options

### Generate Response Configuration Options

| Option       | Description                                                  | Default Value                            |
|--------------|--------------------------------------------------------------|------------------------------------------|
| `content`    | The content to include in the response                       | `"This is a mock response from Ollama."` |
| `doneReason` | The reason why generation completed (e.g., "stop", "length") | `"stop"`                                 |
| `delay`      | The delay before sending the response                        | `Duration.ZERO`                          |

### Chat Response Configuration Options

| Option      | Description                               | Default Value                            |
|-------------|-------------------------------------------|------------------------------------------|
| `content`   | The content to include in the response    | `"This is a mock response from Ollama."` |
| `thinking`  | The thinking process of the model         | `null`                                   |
| `toolCalls` | The tool calls to include in the response | `null`                                   |
| `delay`     | The delay before sending the response     | `Duration.ZERO`                          |

### Embed Response Configuration Options

| Option       | Description                                   | Default Value                                  |
|--------------|-----------------------------------------------|------------------------------------------------|
| `embeddings` | The embeddings to include in the response     | `listOf(listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f))` |
| `embedding`  | A single embedding to include in the response | N/A                                            |
| `model`      | The model name to include in the response     | `null`                                         |
| `delay`      | The delay before sending the response         | `Duration.ZERO`                                |

### Streaming Response Configuration Options

| Option               | Description                                         | Default Value | Availability    |
|----------------------|-----------------------------------------------------|---------------|-----------------|
| `responseFlow`       | A flow of content chunks for the streaming response | `null`        | Generate & Chat |
| `responseChunks`     | A list of content chunks for the streaming response | `null`        | Generate & Chat |
| `delayBetweenChunks` | The delay between sending chunks                    | `0.1.seconds` | Generate & Chat |
| `doneReason`         | The reason why generation completed                 | `"stop"`      | Generate only   |

## Integration Testing

For integration testing, you can use the `AbstractMockOllamaTest` base class:

```kotlin
internal class MyOllamaTest : AbstractMockOllamaTest() {
  @Test
  fun `Should respond to Chat Completion`() = runTest {
    // Configure mock response
    ollama.chat {
      model = modelName
    } responds {
      content("Hello, how can I help you today?")
    }

    // Use your Ollama client to make a request
    val response = myOllamaClient.chat("Hello")

    // Verify the response
    response.content shouldBe "Hello, how can I help you today?"
  }
}
```

## Integration with LangChain4j

AI-Mocks-Ollama can be used with LangChain4j's Ollama integration:

```kotlin
// Create a mock Ollama server
val ollama = MockOllama(verbose = true)

// Configure mock response
ollama.chat {
  model = "llama3"
} responds {
  content("Hello, how can I help you today?")
  delay = 42.milliseconds
}

// Create LangChain4j Ollama client
val model = OllamaChatModel.builder()
  .baseUrl(ollama.baseUrl())
  .modelName("llama3")
  .temperature(0.7)
  .topP(0.9)
  .build()

// Use LangChain4j Kotlin DSL to send a request
val result = model.chat {
  messages += userMessage("Hello")
}

// Verify response
result.apply {
  aiMessage().text() shouldBe "Hello, how can I help you today?"
}
```

Check for examples in
the [integration tests](https://github.com/mokksy/ai-mocks/tree/main/ai-mocks-ollama/src/jvmTest/kotlin/me/kpavlov/aimocks/ollama).
