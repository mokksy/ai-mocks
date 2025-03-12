---
title: "OpenAI"
#weight: 30
toc: true
---

AI-Mocks OpenAI is a specialized mock server implementation for mocking the OpenAI API, built using Mokksy.

`MockOpenai` is tested against official [openai-java SDK](https://github.com/openai/openai-java) and popular JVM AI
frameworks: [LangChain4j](https://github.com/langchain4j/langchain4j)
and [Spring AI](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

Currently, it supports [ChatCompletion](https://platform.openai.com/docs/api-reference/chat/create)
and [Streaming ChatCompletion](https://platform.openai.com/docs/api-reference/chat/streaming) requests.

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

## Basic Usage

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
    .baseUrl(openai.baseUrl()) // connect to mock OpenAI
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

## Mocking Negative Scenarios

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
    .baseUrl(openai.baseUrl())
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

Check for examples in the [integration tests](https://github.com/kpavlov/ai-mocks/tree/main/ai-mocks-openai/src/jvmTest/kotlin/me/kpavlov/aimocks/openai/springai).
