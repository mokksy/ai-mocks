# Mokksy and AI-Mocks

![mokksy-mascot-256.png](mokksy/docs/mokksy-mascot-256.png)

Mock LLM implementations inspired by WireMock, with support for response streaming and SSE. Build, test, and mock OpenAI API responses for development purposes.

[![Kotlin CI with Maven](https://github.com/kpavlov/ai-mocks/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/kpavlov/ai-mocks/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/372f7af6be2144c686b670f5b8708222)](https://app.codacy.com/gh/kpavlov/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Coverage](https://app.codacy.com/project/badge/Coverage/372f7af6be2144c686b670f5b8708222)](https://app.codacy.com/gh/kpavlov/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![Api Docs](https://img.shields.io/badge/api-docs-blue)](https://kpavlov.github.io/ai-mocks/api/)

## Overview

**[Mokksy](mokksy/README.md)**: Mock HTTP Server built using [Kotlin](https://kotlinlang.org/) and [Ktor](Ktor](https://ktor.io/).

**AI-Mocks**: Specialized mock server implementation (e.g., mocking OpenAI API) built using Mokksy.

## Mocking OpenAI API

Set up a mock server and define mock responses:
```kotlin
val openai = MockOpenai(verbose = true)

// Define mock response
openai.completion {
  temperature = temperatureValue
  seed = seedValue
  model = "gpt-4o-mini"
  maxCompletionTokens = maxCompletionTokens
} responds {
  textContent = "Hello"
  finishReason = "stop"
}

// OpenAI client setup
val client: OpenAIClient =
  OpenAIOkHttpClient
    .builder()
    .apiKey("any-key")
    .baseUrl("http://127.0.0.1:${openai.port()}/v1") // connect to mock OpenAI
    .responseValidation(true)
    .build()

// Use the mock endpoint
val params =
  ChatCompletionCreateParams
    .builder()
    .temperature(temperatureValue)
    .maxCompletionTokens(maxCompletionTokens)
    .seed(seedValue.toLong())
    .messages(
      listOf(
        ChatCompletionMessageParam.ofUser(
          ChatCompletionUserMessageParam
            .builder()
            .role(JsonValue.from("user"))
            .content(
              ChatCompletionUserMessageParam.Content.ofText(
                "Just say and nothing else but 'Hello!'",
              ),
            ).build(),
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

## How to run with LangChain4j/Kotlin

You may use also LangChain4J Kotlin Extensions:
```kotlin
val model: OpenAiChatModel =
  OpenAiChatModel
    .builder()
    .apiKey("foo")
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
    temperature = temperature
    model = "gpt-4o-mini"
    userMessage("What is in the sea?")
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
        .temperature(temperature)
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

## How to build

Building project locally:

```shell
mvn clean verify
```

or using Make

```shell
make build
```

## Contributing

We welcome contributions! Please see the [Contributing Guidelines](CONTRIBUTING.md) for details.

