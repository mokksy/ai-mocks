---
title: "OpenAI"
#weight: 30
toc: true
---

AI-Mocks OpenAI is a specialized mock server implementation for mocking the OpenAI API, built using Mokksy.

`MockOpenai` is tested against official [openai-java SDK](https://github.com/openai/openai-java) and popular JVM AI
frameworks: [LangChain4j](https://github.com/langchain4j/langchain4j)
and [Spring AI](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

Currently, it supports:
- [Chat Completions](https://platform.openai.com/docs/api-reference/chat/create) (streaming and non-streaming)
- [Embeddings](https://platform.openai.com/docs/api-reference/embeddings/create)
- [Moderations](https://platform.openai.com/docs/api-reference/moderations/create)

## Quick Start

Add Dependency Include the library in your test dependencies (Maven or Gradle).

{{< tabs "dependencies" >}}
{{< tab "Gradle" >}}
```kotlin
implementation("me.kpavlov.aimocks:ai-mocks-openai-jvm:$latestVersion")
```

{{< /tab >}}
{{< tab "Maven" >}}
```xml
<dependency>
  <groupId>me.kpavlov.aimocks</groupId>
  <artifactId>ai-mocks-openai-jvm</artifactId>
  <version>[LATEST_VERSION]</version>
</dependency>
```

{{< /tab >}}
{{< /tabs >}}

## Chat Completions API

Set up a mock server and define mock responses:

```kotlin
val openai = MockOpenai(verbose = true)
```
Let's simulate OpenAI [Chat Completions API](https://platform.openai.com/docs/api-reference/chat):

```kotlin
// Define mock response
openai.completion {
  temperature = 0.7
  seed = 42
  model = "gpt-4o-mini"
  maxTokens = 100
  topP = 0.95
  systemMessageContains("helpful assistant")
  userMessageContains("say 'Hello!'")
} responds {
  assistantContent = "Hello"
  finishReason = "stop"
  delay = 200.milliseconds // delay before answer
}

// OpenAI client setup
val client: OpenAIClient =
  OpenAIOkHttpClient
    .builder()
    .apiKey("dummy-api-key")
    .baseUrl(openai.baseUrl()) // connect to mock OpenAI
    .responseValidation(true)
    .build()

// Use the mock endpoint
val params =
  ChatCompletionCreateParams
    .builder()
    .temperature(0.7)
    .maxCompletionTokens(100)
    .topP(0.95)
    .seed(42)
    .messages(
      listOf(
        ChatCompletionMessageParam.ofSystem(
          ChatCompletionSystemMessageParam
            .builder()
            .content(
              "You are a helpful assistant.",
            ).build(),
        ),
        ChatCompletionMessageParam.ofUser(
          ChatCompletionUserMessageParam
            .builder()
            .content("Just say 'Hello!' and nothing else")
            .build(),
        ),
      ),
    ).model("gpt-4o-mini")
    .build()

val result: ChatCompletion =
  client
    .chat()
    .completions()
    .create(params)

println(result)
```

## Mocking Negative Scenarios

With AI-Mocks it is possible to test negative scenarios, such as erroneous responses and delays.

```kotlin
openai.completion {
  temperature = 0.7
  seed = 42
  model = "gpt-4o-mini"
  maxTokens = 100
  topP = 0.95
  systemMessageContains("helpful assistant")
  userMessageContains("say 'Hello!'")
} respondsError {
  body =
    // language=json
    """
    {
      "caramba": "Arrr, blast me barnacles! This be not what ye expect! 🏴‍☠️"
    }
    """.trimIndent()
  delay = 1.seconds
  httpStatus = HttpStatusCode.PaymentRequired
}
```

## Integration with LangChain4j

You may use also LangChain4J Kotlin Extensions:

```kotlin
val model: OpenAiChatModel =
  OpenAiChatModel
    .builder()
    .apiKey("dummy-api-key")
    .baseUrl(openai.baseUrl())
    .build()

val result =
  model.chatAsync {
    parameters =
      OpenAiChatRequestParameters
        .builder()
        .temperature(0.7)
        .modelName("gpt-4o-mini")
        .maxCompletionTokens(100)
        .topP(0.95)
        .seed(42)
        .build()
    messages += userMessage("Say Hello")
  }

println(result)
```

### Stream Responses

Mock streaming responses easily with flow support:

```kotlin
// configure mock openai
openai.completion {
  temperature = 0.7
  model = "gpt-4o-mini"
  maxTokens = 100
  topP = 0.95
  topK = 40
  seed = 42
  userMessageContains("What is in the sea?")
} respondsStream {
  responseFlow =
    flow {
      emit("Yellow")
      emit(" submarine")
    }
  finishReason = "stop"
  delay = 60.milliseconds
  delayBetweenChunks = 15.milliseconds

  // send "[DONE]" as last message to finish the stream in openai4j
  sendDone = true
}

// create streaming model (a client)
val model: OpenAiStreamingChatModel =
  OpenAiStreamingChatModel
    .builder()
    .apiKey("foo")
    .baseUrl(openai.baseUrl())
    .build()

// call streaming model
model
  .chatFlow {
    parameters =
      ChatRequestParameters
        .builder()
        .temperature(0.7)
        .modelName("gpt-4o-mini")
        .maxTokens(100)
        .topP(0.95)
        .topK(40)
        .seed(42)
        .build()
    messages += userMessage("What is in the sea?")
  }.collect {
    when (it) {
      is PartialResponse -> {
        println("token = ${it.token}")
      }

      is CompleteResponse -> {
        println("Completed: $it")
      }

      else -> {
        println("Something else = $it")
      }
    }
  }
```

## Integration with Spring-AI

To test Spring-AI integration:

```kotlin
// create mock server
val openai = MockOpenai(verbose = true)

// create Spring-AI client
val chatClient =
  ChatClient
    .builder(
      org.springframework.ai.openai.OpenAiChatModel
        .builder()
        .openAiApi(
          OpenAiApi
            .builder()
            .apiKey("demo-key")
            .baseUrl(openai.baseUrl())
            .build(),
        ).build(),
    ).build()

// Set up a mock for the LLM call
openai.completion {
  temperature = 0.7
  seed = 42
  model = "gpt-4o-mini"
  maxTokens = 100
  topP = 0.95
  topK = 40
  systemMessageContains("helpful pirate")
  userMessageContains("say 'Hello!'")
} responds {
  assistantContent = "Ahoy there, matey! Hello!"
  finishReason = "stop"
  delay = 200.milliseconds
}

// Configure Spring-AI client call
val response =
  chatClient
    .prompt()
    .system("You are a helpful pirate")
    .user("Just say 'Hello!'")
    .options<OpenAiChatOptions>(
      OpenAiChatOptions
        .builder()
        .maxCompletionTokens(100)
        .temperature(0.7)
        .topP(0.95)
        .topK(40)
        .model("gpt-4o-mini")
        .seed(42)
        .build(),
    )
    // Make a call
    .call()
    .chatResponse()

// Verify the response
response?.result shouldNotBe null
response?.result?.apply {
metadata.finishReason shouldBe "STOP"
output?.text shouldBe "Ahoy there, matey! Hello!"
}
```

Check for examples in the [integration tests](https://github.com/mokksy/ai-mocks/tree/main/ai-mocks-openai/src/jvmTest/kotlin/me/kpavlov/aimocks/openai/springai).

## Embeddings API

Mock the OpenAI [Embeddings API](https://platform.openai.com/docs/api-reference/embeddings) to test your embeddings generation:

### Basic Embedding Response

```kotlin
// Set up mock server
val openai = MockOpenai(verbose = true)

// Define mock response for embedding request
openai.embeddings {
    model = "text-embedding-3-small"
    stringInput("Hello world")
} responds {
    delay = 200.milliseconds
    embeddings(
        listOf(0.1f, 0.2f, 0.3f)
    )
}

// Create OpenAI client
val client: OpenAIClient =
    OpenAIOkHttpClient
        .builder()
        .apiKey("dummy-key")
        .baseUrl(openai.baseUrl())
        .responseValidation(true)
        .build()

// Make embedding request
val params = EmbeddingCreateParams
    .builder()
    .model("text-embedding-3-small")
    .input(EmbeddingCreateParams.Input.ofString("Hello world"))
    .build()

val result = client
    .embeddings()
    .create(params)

// Verify results
result.model() // "text-embedding-3-small"
result.data()[0].embedding() // [0.1, 0.2, 0.3]
result.data()[0].index() // 0
```

### Multiple Embeddings

You can mock multiple embeddings for batch input:

```kotlin
openai.embeddings {
    model = "text-embedding-3-small"
    stringListInput(listOf("Hello", "world"))
} responds {
    delay = 100.milliseconds
    embeddings(
        listOf(0.1f, 0.2f, 0.3f),
        listOf(0.4f, 0.5f, 0.6f)
    )
}

val params = EmbeddingCreateParams
    .builder()
    .model("text-embedding-3-small")
    .input(EmbeddingCreateParams.Input.ofArrayOfStrings(listOf("Hello", "world")))
    .build()

val result = client
    .embeddings()
    .create(params)

// Returns 2 embeddings
result.data().size // 2
result.data()[0].embedding() // [0.1, 0.2, 0.3]
result.data()[1].embedding() // [0.4, 0.5, 0.6]
```

### Error Scenarios

Test error handling for embeddings:

```kotlin
openai.embeddings {
    model = "text-embedding-3-small"
    stringInput("invalid input")
} respondsError(String::class) {
    body = "Invalid input!"
    contentType = ContentType.Text.Plain
    httpStatus = HttpStatusCode.BadRequest
    delay = 200.milliseconds
}

// This will throw BadRequestException
val params = EmbeddingCreateParams
    .builder()
    .model("text-embedding-3-small")
    .input(EmbeddingCreateParams.Input.ofString("invalid input"))
    .build()

try {
    client.embeddings().create(params)
} catch (e: BadRequestException) {
    // Handle error
}
```
