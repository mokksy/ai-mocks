# AI-Mocks-Ollama

A mock server implementation for the [Ollama API](https://github.com/ollama/ollama/blob/main/docs/api.md) built on top
of Mokksy.

## Overview

AI-Mocks-Ollama provides a mock server that simulates the Ollama API for testing purposes. It supports the main
endpoints of the Ollama API, including:

- Generate completions
- Chat completions
- Model management
- Embeddings

## Usage

### Basic Setup

```kotlin
// Create a mock Ollama server
val ollama = MockOllama(verbose = true)

// Configure a mock response for the generate endpoint
ollama.generate {
  model = "llama3"
  prompt = "Tell me a joke"
} responds {
  content("Why did the chicken cross the road? To get to the other side!")
  doneReason("stop")
}

// Configure a mock response for the chat endpoint
ollama.chat {
  model = "llama3"
} responds {
  content("Hello, how can I help you today?")
}

// Get the base URL of the mock server
val baseUrl = ollama.baseUrl()
```

### Integration Testing

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

### Streaming Responses

AI-Mocks-Ollama supports streaming responses for both generate and chat endpoints:

```kotlin
ollama.generate {
  model = "llama3"
  prompt = "Tell me a story"
  stream = true
} respondsStream {
  responseChunks = listOf(
    "Once upon a time",
    " in a land far, far away",
    " there lived a programmer",
    " who never had to debug in production."
  )
  delayBetweenChunks = 100.milliseconds
}
```

## Features

- **Full API Support**: Implements all the main endpoints of the Ollama API.
- **Streaming**: Supports streaming responses for generate and chat endpoints.
- **Flexible Matching**: Match requests based on model, prompt, messages, and other parameters.
- **Customizable Responses**: Configure response content, timing, and error conditions.
- **Integration Testing**: Base class for integration testing with the mock server.

## Examples

See the integration tests for examples of how to use AI-Mocks-Ollama:

[ChatCompletionTest.kt](src/jvmTest/kotlin/me/kpavlov/aimocks/ollama/ktor/ChatCompletionTest.kt)
[GenerateCompletionTest.kt](src/jvmTest/kotlin/me/kpavlov/aimocks/ollama/ktor/GenerateCompletionTest.kt)
