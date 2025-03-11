# MockAnthropic

[MockAnthropic](src/commonMain/kotlin/me/kpavlov/aimocks/anthropic/MockAnthropic.kt) provides a local mock server for simulating [Anthropic API endpoints](https://docs.anthropic.com/en/api). It simplifies testing by allowing you to define request expectations and responses without making real network calls.

## Quick Start

1. **Add Dependency**
   Include the library in your test dependencies (Maven or Gradle).

    Maven:
    ```xml
    <dependency>
        <groupId>me.kpavlov.aimocks</groupId>
        <artifactId>ai-mocks-anthropic-jvm</artifactId>
        <version>[LATEST_VERSION]</version>
    </dependency>
    ```
   Gradle:
    ```kotlin
    implementation("me.kpavlov.aimocks:ai-mocks-anthropic-jvm:$latestVersion")
    ```

2. **Initialize the Server**
   ```kotlin
   val anthropic = MockAnthropic(verbose = true)
   ```
  - The server will start on a random free port by default.
  - You can retrieve the server’s base URL via `anthropic.baseUrl()`.

3. **Configure Requests and Responses**

   Here’s an example that sets up a mock “messages” endpoint and defines the response:
    ```kotlin
    anthropic.messages {
        temperature = 0.42
        model = "claude-3-7-sonnet-latest"
        maxCompletionTokens = 100
        systemMessageContains("helpful assistant")
        userMessageContains("say 'Hello!'")
    } responds {
        assistantContent = "Hello" // response content
        delay = 200.milliseconds // simulate delay
    }
    ```
    - The `messages { ... }` block sets how the incoming request must look.
    - The `responds { ... }` block defines what the mock server returns.


4. **Calling Anthropic API Client**

    Here’s an example that sets up and call officoal [Anthropic SDK client](https://github.com/anthropics/anthropic-sdk-java):
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

### Streaming Responses

You can also configure streaming responses (such as chunked SSE events) for testing:

```kotlin
anthropic.messages {
    // request setup
} respondsStream {
    // Provide chunks or a Flow<String>
    responseChunks = listOf("first chunk", "second chunk")
    // Additional streaming settings like delays, finish reason, etc.
}
```

Use your Anthropic client to invoke the endpoint at `anthropic.baseUrl()`, and it will receive a streamed response.

### Error Simulation

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
        "message": "An unexpected error has occurred internal to Anthropic’s systems."
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

---

That’s it! You have a local mock server simulating Anthropic endpoints, ideal for straightforward and streaming tests alike. Feel free to configure multiple scenarios, vary the responses, and streamline your testing workflow.
