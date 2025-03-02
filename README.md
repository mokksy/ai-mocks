# Mokksy and AI-Mocks

[![Maven Central](https://img.shields.io/maven-central/v/me.kpavlov.aimocks/ai-mocks-openai)](https://repo1.maven.org/maven2/me/kpavlov/aimocks/ai-mocks-openai/)
[![Kotlin CI](https://github.com/kpavlov/ai-mocks/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/kpavlov/ai-mocks/actions/workflows/gradle.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/4887b8978534404dbc62c4894b630501)](https://app.codacy.com/gh/kpavlov/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Coverage](https://app.codacy.com/project/badge/Coverage/4887b8978534404dbc62c4894b630501)](https://app.codacy.com/gh/kpavlov/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![codecov](https://codecov.io/github/kpavlov/ai-mocks/graph/badge.svg?token=449G80QY5S)](https://codecov.io/github/kpavlov/ai-mocks)
[![Api Docs](https://img.shields.io/badge/api-docs-blue)](https://kpavlov.github.io/ai-mocks/api/)

_Mokksy_ and _AI-Mocks_ are mock HTTP and LLM (Large Language Model) servers inspired by WireMock, with support for
response streaming and Server-Side Events (SSE). They are designed to build, test, and mock OpenAI API responses for
development purposes.

# Mokksy

![mokksy-mascot-256.png](mokksy/docs/mokksy-mascot-256.png)

**[Mokksy](mokksy/README.md)** is a mock HTTP server built with [Kotlin](https://kotlinlang.org/)
and [Ktor](https://ktor.io/). It addresses the limitations of WireMock by supporting true SSE and streaming responses,
making it particularly useful for integration testing LLM clients.

## Core Features

- Flexibility to control server response directly via ApplicationCall object.
- Built with Kotest Assertions.
- Fluent modern Kotlin DSL API.
- Support for simulating streamed responses and Server-Side Events (SSE) with delays between chunks.
- Support for simulating response delays.
- Supports [serialization](https://ktor.io/docs/server-serialization.html#add_json_dependency) with [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) and [Jackson](https://github.com/FasterXML/jackson) (only on JVM). _Kotlinx.serialization_ serializer/deserializer will attempt first, then _Jackson_ will try its best as a fallback. This ensures that [@Serializable](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-core/kotlinx.serialization/-serializable/) annotation is honored.
## Example Usages

### Responding with Predefined Responses

```kotlin
// given
val expectedResponse =
  // language=json
  """
    {
        "response": "Pong"
    }
    """.trimIndent()

mokksy.get {
  path = beEqual("/ping")
  containsHeader("Foo", "bar")
} respondsWith {
  body = expectedResponse
}

// when
val result = client.get("/ping") {
  headers.append("Foo", "bar")
}

// then
assertThat(result.status).isEqualTo(HttpStatusCode.OK)
assertThat(result.bodyAsText()).isEqualTo(expectedResponse)
```

### POST Request

```kotlin
// given
val id = Random.nextInt()
val expectedResponse =
  // language=json
  """
    {
        "id": "$id",
        "name": "thing-$id"
    }
    """.trimIndent()

mokksy.post {
  path = beEqual("/things")
  bodyContains("\"$id\"")
} respondsWith {
  body = expectedResponse
  httpStatus = HttpStatusCode.Created
  headers {
    // type-safe builder style
    append(HttpHeaders.Location, "/things/$id")
  }
  headers += "Foo" to "bar" // list style
}

// when
val result =
  client.post("/things") {
    headers.append("Content-Type", "application/json")
    setBody(
      // language=json
      """
            {
                "id": "$id"
            }
            """.trimIndent(),
    )
  }

// then
assertThat(result.status).isEqualTo(HttpStatusCode.Created)
assertThat(result.bodyAsText()).isEqualTo(expectedResponse)
assertThat(result.headers["Location"]).isEqualTo("/things/$id")
assertThat(result.headers["Foo"]).isEqualTo("bar")
```

### Server-Side Events (SSE) Response

Server-Side Events (SSE) is a technology that allows a server to push updates to the client over a single, long-lived
HTTP connection, enabling real-time updates without requiring the client to continuously poll the server for new data.

```kotlin
mokksy.post {
  path = beEqual("/sse")
} respondsWithSseStream {
  flow =
    flow {
      delay(200.milliseconds)
      emit(
        ServerSentEvent(
          data = "One",
        ),
      )
      delay(50.milliseconds)
      emit(
        ServerSentEvent(
          data = "Two",
        ),
      )
    }
}

// when
val result = client.post("/sse")

// then
assertThat(result.status)
  .isEqualTo(HttpStatusCode.OK)
assertThat(result.contentType())
  .isEqualTo(ContentType.Text.EventStream.withCharsetIfNeeded(Charsets.UTF_8))
assertThat(result.bodyAsText())
  .isEqualTo("data: One\r\ndata: Two\r\n")
```

# AI-Mocks

**AI-Mocks** is a specialized mock server implementations (e.g., mocking OpenAI API) built using Mokksy.

## Mocking OpenAI API

`MockOpenai` is tested against official [openai-java SDK](https://github.com/openai/openai-java) and popular JVM AI
frameworks: [LangChain4j](https://github.com/langchain4j/langchain4j)
and [Spring AI](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

Currently, it only supports [ChatCompletion](https://platform.openai.com/docs/api-reference/chat/create)
and [Streaming ChatCompletion](https://platform.openai.com/docs/api-reference/chat/streaming) requests.

Set up a mock server and define mock responses:

```kotlin
val openai = MockOpenai(verbose = true)

// Define mock response
openai.completion {
  temperature = temperatureValue
  seed = seedValue
  model = "gpt-4o-mini"
  maxCompletionTokens = maxCompletionTokensValue
  systemMessageContains("helpful assistant")
  userMessageContains("say 'Hello!'")
} responds {
  assistantContent = "Hello"
  finishReason = "stop"
}

// OpenAI client setup
val client: OpenAIClient =
  OpenAIOkHttpClient
    .builder()
    .apiKey("dummy-api-key")
    .baseUrl("http://127.0.0.1:${openai.port()}/v1") // connect to mock OpenAI
    .responseValidation(true)
    .build()

// Use the mock endpoint
val params =
  ChatCompletionCreateParams
    .builder()
    .temperature(temperatureValue)
    .maxCompletionTokens(maxCompletionTokensValue)
    .seed(seedValue.toLong())
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
    ).model(ChatModel.GPT_4O_MINI)
    .build()

val result: ChatCompletion =
  client
    .chat()
    .completions()
    .create(params)

println(result)
```

## Mocking negative scenarios

With AI-Mocks it is possible to test negative scenarios, such as erroneous responses and delays.

```kotlin
openai.completion {
  temperature = temperatureValue
  seed = seedValue
  model = modelName
  maxCompletionTokens = maxCompletionTokensValue
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

## How to test LangChain4j/Kotlin

You may use also LangChain4J Kotlin Extensions:

```kotlin
val model: OpenAiChatModel =
  OpenAiChatModel
    .builder()
    .apiKey("dummy-api-key")
    .baseUrl("http://127.0.0.1:${openai.port()}/v1")
    .build()

val result =
  model.chatAsync {
    parameters =
      OpenAiChatRequestParameters
        .builder()
        .temperature(temperature)
        .modelName("gpt-4o-mini")
        .seed(seedValue)
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
  temperature = temperatureValue
  model = "gpt-4o-mini"
  userMessageContains("What is in the sea?")
} respondsStream {
  responseFlow =
    flow {
      emit("Yellow")
      emit(" submarine")
    }
  finishReason = "stop"

  // send "[DONE]" as last message to finish the stream in openai4j
  sendDone = true
}

// create streaming model (a client)
val model: OpenAiStreamingChatModel =
  OpenAiStreamingChatModel
    .builder()
    .apiKey("foo")
    .baseUrl("http://127.0.0.1:${openai.port()}/v1")
    .build()

// call streaming model
model
  .chatFlow {
    parameters =
      ChatRequestParameters
        .builder()
        .temperature(temperatureValue)
        .modelName("gpt-4o-mini")
        .build()
    messages += userMessage(userMessage)
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

## How to test Spring-AI

To test Spring-AI integration run:

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
            .baseUrl("http://127.0.0.1:${openai.port()}")
            .build(),
        ).build(),
    ).build()

// Set up a mock for the LLM call
openai.completion {
  temperature = temperatureValue
  seed = seedValue
  model = modelName
  maxCompletionTokens = maxCompletionTokensValue
  systemMessageContains("helpful pirate")
  userMessageContains("say 'Hello!'")
} responds {
  assistantContent = "Ahoy there, matey! Hello!"
  finishReason = "stop"
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
        .maxCompletionTokens(maxCompletionTokensValue.toInt())
        .temperature(temperatureValue)
        .model(modelName)
        .seed(seedValue)
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

Check for examples in
the [integration tests](https://github.com/kpavlov/ai-mocks/tree/main/ai-mocks-openai/src/jvmTest/kotlin/me/kpavlov/aimocks/openai/springai).

## How to build

Building project locally:

```shell
gradle build
```

or using Make:

```shell
make
```

## Contributing

I do welcome contributions! Please see the [Contributing Guidelines](CONTRIBUTING.md) for details.

## Enjoying LLM integration testing? :heart:

[![Buy me a Coffee](https://cdn.buymeacoffee.com/buttons/default-orange.png)](https://buymeacoffee.com/mailsk)
