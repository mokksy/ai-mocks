---
title: "Anthropic"
#weight: 40
toc: true
---

[MockAnthropic](https://github.com/kpavlov/ai-mocks/blob/main/ai-mocks-anthropic/src/commonMain/kotlin/me/kpavlov/aimocks/anthropic/MockAnthropic.kt) provides a local mock server for simulating [Anthropic API endpoints](https://docs.anthropic.com/en/api). It simplifies testing by allowing you to define request expectations and responses without making real network calls.

## Quick Start

1. **Add Dependency**
   Include the library in your test dependencies (Maven or Gradle).

    {{< tabs "dependencies" >}}
    {{< tab "Gradle" >}}
```kotlin
implementation("me.kpavlov.aimocks:ai-mocks-anthropic-jvm:$latestVersion")
```
    {{< /tab >}}
    {{< tab "Maven" >}}
```xml
<dependency>
    <groupId>me.kpavlov.aimocks</groupId>
    <artifactId>ai-mocks-anthropic-jvm</artifactId>
    <version>[LATEST_VERSION]</version>
</dependency>
```
    {{< /tab >}}
    {{< /tabs >}}


2. **Initialize the Server**
   ```kotlin
   val anthropic = MockAnthropic(verbose = true)
   ```
  - The server will start on a random free port by default.
  - You can retrieve the server's base URL via `anthropic.baseUrl()`.

3. **Configure Requests and Responses**

   Here's an example that sets up a mock "messages" endpoint and defines the response:
    ```kotlin
    anthropic.messages {
        temperature = 0.42
        model = "claude-3-7-sonnet-latest"
        maxTokens = 100
        topP = 0.95
        topK = 40
        userId = "user123"
        systemMessageContains("helpful assistant")
        userMessageContains("say 'Hello!'")
    } responds {
        messageId = "msg_1234567890"
        assistantContent = "Hello" // response content
        delay = 200.milliseconds // simulate delay
        stopReason = "end_turn" // reason for stopping
    }
    ```
    - The `messages { ... }` block sets how the incoming request must look.
    - The `responds { ... }` block defines what the mock server returns.


4. **Calling Anthropic API Client**

    Here's an example that sets up and call official [Anthropic SDK client](https://github.com/anthropics/anthropic-sdk-java):
    ```kotlin
    // create Anthropic SDK client
    val client =
        AnthropicOkHttpClient
            .builder()
            .apiKey("my-anthropic-api-key")
            .baseUrl(anthropic.baseUrl())
            .build()

    // prepare Anthropic SDK call
    val params =
        MessageCreateParams
            .builder()
            .temperature(0.42)
            .maxTokens(100)
            .system("You are a helpful assistant.")
            .addUserMessage("Just say 'Hello!' and nothing else")
            .model("claude-3-7-sonnet-latest")
            .build()

    val result =
        client
            .messages()
            .create(params)

    result
        .content()
        .first()
        .asText()
        .text() shouldBe "Hello" // kotest matcher
    ```

## Streaming Responses

You can also configure streaming responses (such as chunked SSE events) for testing:

```kotlin
anthropic.messages {
  temperature = 0.7
  model = "claude-3-7-sonnet-latest"
  maxTokens = 150
  topP = 0.95
  topK = 40
  userId = "user123"
  systemMessageContains("person from 60s")
  userMessageContains("What do we need?")
} respondsStream {
  responseChunks = listOf("All", " we", " need", " is", " Love")
  delay = 50.milliseconds
  delayBetweenChunks = 10.milliseconds
  stopReason = "end_turn"
}
```

Or, you can use a flow to generate the response:
```kotlin
anthropic.messages("anthropic-messages-flow") {
  temperature = 0.7
  model = "claude-3-7-sonnet-latest"
  maxTokens = 150
  topP = 0.95
  topK = 40
  userId = "user123"
  systemMessageContains("person from 60s")
  userMessageContains("What do we need?")
} respondsStream {
  responseFlow =
    flow {
      emit("All")
      emit(" we")
      emit(" need")
      emit(" is")
      emit(" Love")
    }
  delay = 60.milliseconds
  delayBetweenChunks = 15.milliseconds
  stopReason = "end_turn"
}
```

Call Anthropic client:
```kotlin
val params =
  MessageCreateParams
    .builder()
    .temperature(0.7)
    .maxTokens(150)
    .topP(0.95)
    .topK(40)
    .metadata(Metadata.builder().userId("user123").build())
    .system("You are a person from 60s")
    .addUserMessage("What do we need?")
    .model("claude-3-7-sonnet-latest")
    .build()

val timedValue =
  measureTimedValue {
    client
      .messages()
      .createStreaming(params)
      .stream() // streaming
      .consumeAsFlow()
      .onStart { logger.info { "Started streaming" } }
      .onEach {
        logger
          .info { it }
      }.onCompletion { logger.info { "Completed streaming" } }
      .count()
  }
timedValue.duration shouldBeLessThan 10.seconds
timedValue.value shouldBeLessThan 10
```

Use your Anthropic client to invoke the endpoint at `anthropic.baseUrl()`, and it will receive a streamed response.

## Error Simulation

To test client behavior for exceptional cases:

```kotlin
anthropic.messages {
    // expected request
} respondsError {
    httpStatus = HttpStatusCode.InternalServerError // Set an error status code
    body = """{
      "type": "error",
      "error": {
        "type": "api_error",
        "message": "An unexpected error has occurred internal to Anthropic's systems."
      }
    }"""
    // Optionally add a delay or other properties
}
```

## Practical Example in Tests

```kotlin
@Test
fun `test basic conversation`() {
    // Arrange: mock the messages API
    anthropic.messages {
        userMessageContains("Hello")
    } responds {
        assistantContent = "Hi from mock!"
    }

    // Act: call the mocked endpoint in your test code
    val result = yourAnthropicClient.sendMessage("Hello")

    // Assert: verify the response
    assertEquals("Hi from mock!", result.assistantMessage)
}
```

## Stopping the Server

```kotlin
anthropic.stop()
```

Stops the mock server and frees up resources.
