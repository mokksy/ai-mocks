---
title: "Gemini"
#weight: 30
toc: true
---

AI-Mocks Gemini is a specialized mock server implementation for mocking the Google Vertex AI Gemini API, built using Mokksy.

`MockGemini` is tested against the Spring AI framework with the Vertex AI Gemini integration.

Currently, it supports basic content generation requests and streaming responses.

## Quick Start

Include the library in your test dependencies (Maven or Gradle).

{{< tabs "dependencies" >}}
{{< tab "Gradle" >}}
```kotlin
testImplementation("dev.mokksy.aimocks:ai-mocks-gemini-jvm:$latestVersion")
```
    {{< /tab >}}
    {{< tab "Maven" >}}
```xml
<dependency>
  <groupId>dev.mokksy.aimocks</groupId>
  <artifactId>ai-mocks-gemini-jvm</artifactId>
  <version>[LATEST_VERSION]</version>
  <scope>test</scope>
</dependency>
```

{{< /tab >}}
{{< /tabs >}}

## Content Generation API

Set up a mock server and define mock responses:

<!--- CLEAR -->
<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
-->
```kotlin
val gemini = MockGemini(verbose = true)
```

<!--- KNIT example-gemini-01.kt -->
Let's simulate Gemini content generation API:

<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
import kotlin.time.Duration.Companion.milliseconds
val gemini = MockGemini(verbose = true)
fun main() {
-->
```kotlin
// Define mock response
gemini.generateContent {
  temperature = 0.7
  model = "gemini-2.0-flash"
  project = "your-project-id"
  location = "us-central1"
  apiVersion = "v1beta1"
  path = null // custom request path, overrides "apiVersion"
  seed = 42
  maxTokens = 100
  topK = 40
  topP = 0.95
  maxOutputTokens(200)
  systemMessageContains("helpful pirate")
  userMessageContains("say 'Hello!'")
  requestBodyContains("helpful")
  requestBodyContainsIgnoringCase("PIRATE")
  requestBodyDoesNotContains("unwanted text")
  requestBodyDoesNotContainsIgnoringCase("unwanted case insensitive text")
  requestMatchesPredicate { it.generationConfig?.topP == 0.95 }
} responds {
  content = "Ahoy there, matey! Hello!"
  finishReason = "stop"
  role = "model"
  delay = 42.milliseconds // delay before answer
}
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-02.kt -->

### Configuration Options

The following tables list all available configuration options for mocking Gemini API calls.

#### Request Configuration Options

| Option                                   | Description                                                                        |
|------------------------------------------|------------------------------------------------------------------------------------|
| `temperature`                            | Controls randomness of the output. Lower values make output more deterministic.    |
| `model`                                  | The Gemini model to use.                                                           |
| `maxTokens`                              | Maximum number of tokens to generate.                                              |
| `topK`                                   | Limits token selection to the K most likely next tokens.                           |
| `topP`                                   | Limits token selection to tokens with cumulative probability of P.                 |
| `project`                                | Google Cloud project ID.                                                           |
| `location`                               | Google Cloud location.                                                             |
| `apiVersion`                             | API version to use.                                                                |
| `path`                                   | Custom request path.                                                               |
| `seed`                                   | Seed for deterministic generation.                                                 |
| `maxOutputTokens`                        | Maximum number of tokens to generate.                                              |
| `systemMessageContains`                  | Matches requests with system messages containing the specified text.               |
| `userMessageContains`                    | Matches requests with user messages containing the specified text.                 |
| `requestBodyContains`                    | Matches requests with bodies containing the specified text.                        |
| `requestBodyContainsIgnoringCase`        | Matches requests with bodies containing the specified text (case-insensitive).     |
| `requestBodyDoesNotContains`             | Matches requests with bodies not containing the specified text.                    |
| `requestBodyDoesNotContainsIgnoringCase` | Matches requests with bodies not containing the specified text (case-insensitive). |
| `requestMatchesPredicate`                | Matches requests satisfying a custom predicate.                                    |

#### Response Configuration Options

| Option         | Description                                            | Default Value                                |
|----------------|--------------------------------------------------------|----------------------------------------------|
| `content`      | The content to include in the response.                | `"This is a mock response from Gemini API."` |
| `finishReason` | The reason why the model stopped generating tokens.    | `"STOP"`                                     |
| `role`         | The role of the content.                               | `"model"`                                    |
| `delay`        | The delay before sending the response.                 | `Duration.ZERO`                              |
| `delayMillis`  | The delay before sending the response in milliseconds. | N/A                                          |

#### Streaming Content Generation

Here's an example of setting up a streaming content generation mock:

<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds
val gemini = MockGemini(verbose = true)
fun main() {
-->
```kotlin
// Define streaming mock response
gemini.generateContentStream {
  temperature = 0.7
  model = "gemini-2.0-flash"
  project = "your-project-id"
  location = "us-central1"
  apiVersion = "v1beta1"
  seed = 42
  maxTokens = 100
  topK = 40
  topP = 0.95
  maxOutputTokens(200)
  systemMessageContains("helpful pirate")
  userMessageContains("say 'Hello!'")
} respondsStream {
  responseFlow = flow {
    emit("Ahoy")
    emit(" there,")
    delay(100.milliseconds)
    emit(" matey!")
    emit(" Hello!")
  }
  // Alternatively, you can use responseChunks = listOf("Ahoy", " there,", " matey!", " Hello!")
  // Or chunks("Ahoy", " there,", " matey!", " Hello!")
  finishReason = "stop"
  delay = 60.milliseconds // delay before first chunk
  delayBetweenChunks = 15.milliseconds // delay between chunks
}
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-03.kt -->

#### Streaming Response Configuration Options

| Option               | Description                                                    | Default Value   |
|----------------------|----------------------------------------------------------------|-----------------|
| `responseFlow`       | A flow of content chunks to include in the streaming response. | `null`          |
| `responseChunks`     | A list of content chunks to include in the streaming response. | `null`          |
| `chunks`             | Sets the chunks of content for the streaming response.         | N/A             |
| `delayBetweenChunks` | The delay between sending chunks.                              | `Duration.ZERO` |
| `finishReason`       | The reason why the model stopped generating tokens.            | `"STOP"`        |

## Integration with Spring-AI

First, we need a function to create VertexAI client, configured to use the arbitrary server endpoint and credentials.

<!--- INCLUDE
import com.google.api.gax.core.NoCredentialsProvider
import com.google.auth.ApiKeyCredentials
import com.google.cloud.vertexai.Transport
import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.api.LlmUtilityServiceClient
import com.google.cloud.vertexai.api.LlmUtilityServiceSettings
import com.google.cloud.vertexai.api.PredictionServiceClient
import com.google.cloud.vertexai.api.PredictionServiceSettings
import com.google.cloud.vertexai.api.stub.LlmUtilityServiceStubSettings
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.toJavaDuration
-->
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

<!--- KNIT example-gemini-04.kt -->

Then we should create `MockGemini` server and test Spring-AI integration:

<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions
import com.google.cloud.vertexai.VertexAI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
fun createTestVertexAI(endpoint: String, projectId: String, location: String, timeout: Duration): VertexAI = TODO()
fun main() {
-->
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
    .options(VertexAiGeminiChatOptions.builder().temperature(0.7).build())
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

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-05.kt -->

## Streaming Responses

Mock streaming responses easily with flow support:

<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions
import com.google.cloud.vertexai.VertexAI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
fun createTestVertexAI(endpoint: String, projectId: String, location: String, timeout: Duration): VertexAI = TODO()
val gemini = MockGemini(verbose = true)
val vertexAI = createTestVertexAI(
    endpoint = gemini.baseUrl(),
    projectId = "your-project-id",
    location = "us-central1",
    timeout = 10.seconds,
)
val chatClient = ChatClient.builder(VertexAiGeminiChatModel.builder().vertexAI(vertexAI).build()).build()
fun main() {
-->
```kotlin
// configure mock gemini
gemini.generateContentStream {
  temperature = 0.7
  model = "gemini-2.0-flash"
  project = "your-project-id"
  location = "us-central1"
  systemMessageContains("You are a helpful pirate")
  userMessageContains("Just say 'Hello!'")
}.respondsStream(sse = false) {
  responseFlow =
    flow {
      emit("Ahoy")
      emit(" there,")
      delay(100.milliseconds)
      emit(" matey!")
      emit(" Hello!")
    }
  delay = 60.milliseconds
  delayBetweenChunks = 50.milliseconds
}

// Use Spring AI's streaming API
val buffer = StringBuffer()
val chunkCount =
  chatClient
    .prompt()
    .system("You are a helpful pirate")
    .user("Just say 'Hello!'")
    .options(VertexAiGeminiChatOptions.builder().temperature(0.7).build())
    .stream()
    .chatResponse()
    .doOnNext { chunk ->
      // Process each chunk as it arrives
      chunk.result.output.text?.let(buffer::append)
    }.count()
    .block(5.seconds.toJavaDuration())

// Verify the complete response
buffer.toString() shouldBe "Ahoy there, matey! Hello!"
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-06.kt -->

## Integration with Google Gen AI Java SDK

AI-Mocks Gemini can also be used to test applications that use
the [Google Gen AI Java SDK](https://github.com/googleapis/java-genai) directly.

### Setting up the Client

First, create a mock Gemini server:

<!--- CLEAR -->
<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
-->
```kotlin
val gemini = MockGemini(verbose = true)
```

<!--- KNIT example-gemini-07.kt -->

Then, configure the Google Gen AI Java SDK client to use the mock server:

<!--- INCLUDE
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.genai.Client
import com.google.genai.types.HttpOptions
import dev.mokksy.aimocks.gemini.MockGemini
val gemini = MockGemini(verbose = true)
fun main() {
-->
```kotlin
val client = Client.builder()
  .project("your-project-id")
  .location("us-central1")
  .credentials(
    GoogleCredentials.create(
      AccessToken.newBuilder().setTokenValue("dummy-token").build()
    )
  )
  .vertexAI(true)
  .httpOptions(HttpOptions.builder().baseUrl(gemini.baseUrl()).build())
  .build()
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-08.kt -->

### Regular Content Generation

Set up a mock response for a regular content generation request:

<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
import kotlin.time.Duration.Companion.milliseconds
val gemini = MockGemini(verbose = true)
fun main() {
-->
```kotlin
gemini.generateContent {
  temperature = 0.7
  seed = 42
  model = "gemini-2.0-flash"
  project = "your-project-id"
  location = "us-central1"
  apiVersion = "v1beta1"
  systemMessageContains("You are a helpful pirate")
  userMessageContains("Just say 'Hello!'")
} responds {
  content = "Ahoy there, matey! Hello!"
  delay = 60.milliseconds
}
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-09.kt -->

Make a request using the Google Gen AI Java SDK:

<!--- INCLUDE
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.HttpOptions
import com.google.genai.types.Part
import dev.mokksy.aimocks.gemini.MockGemini
import io.kotest.matchers.shouldBe
val gemini = MockGemini(verbose = true)
val client = Client.builder()
    .project("your-project-id")
    .location("us-central1")
    .credentials(
        GoogleCredentials.create(
            AccessToken.newBuilder().setTokenValue("dummy-token").build()
        )
    )
    .vertexAI(true)
    .httpOptions(HttpOptions.builder().baseUrl(gemini.baseUrl()).build())
    .build()
fun main() {
-->
```kotlin
val config = GenerateContentConfig.builder()
  .seed(42)
  .maxOutputTokens(100)
  .temperature(0.7f)
  .systemInstruction(
    Content.builder().role("system")
      .parts(Part.fromText("You are a helpful pirate")).build()
  )
  .build()

val response = client.models.generateContent(
  "gemini-2.0-flash",
  "Just say 'Hello!'",
  config
)

// Verify the response
response.text() shouldBe "Ahoy there, matey! Hello!"
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-10.kt -->

### Streaming Content Generation

Set up a mock response for a streaming content generation request:

<!--- INCLUDE
import dev.mokksy.aimocks.gemini.MockGemini
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds
val gemini = MockGemini(verbose = true)
fun main() {
-->
```kotlin
gemini.generateContentStream {
  temperature = 0.7
  apiVersion = "v1beta1"
  location = "us-central1"
  maxOutputTokens(100)
  model = "gemini-2.0-flash"
  project = "your-project-id"
  seed = 42
  systemMessageContains("You are a helpful pirate")
  userMessageContains("Just say 'Hello!'")
} respondsStream {
  responseFlow =
    flow {
      emit("Ahoy")
      emit(" there,")
      delay(100.milliseconds)
      emit(" matey!")
      emit(" Hello!")
    }
  delay = 60.milliseconds
  delayBetweenChunks = 15.milliseconds
}
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-11.kt -->

Make a streaming request using the Google Gen AI Java SDK:

<!--- INCLUDE
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.HttpOptions
import com.google.genai.types.Part
import dev.mokksy.aimocks.gemini.MockGemini
import io.kotest.matchers.shouldBe
val gemini = MockGemini(verbose = true)
val client = Client.builder()
    .project("your-project-id")
    .location("us-central1")
    .credentials(
        GoogleCredentials.create(
            AccessToken.newBuilder().setTokenValue("dummy-token").build()
        )
    )
    .vertexAI(true)
    .httpOptions(HttpOptions.builder().baseUrl(gemini.baseUrl()).build())
    .build()
val config = GenerateContentConfig.builder()
    .seed(42)
    .maxOutputTokens(100)
    .temperature(0.7f)
    .systemInstruction(
        Content.builder().role("system")
            .parts(Part.fromText("You are a helpful pirate")).build()
    )
    .build()
fun main() {
-->
```kotlin
val response = client.models.generateContentStream(
  "gemini-2.0-flash",
  "Just say 'Hello!'",
  config
)

// Collect and verify the streaming response
val fullResponse = response.joinToString(separator = "") {
  it.text() ?: ""
}
fullResponse shouldBe "Ahoy there, matey! Hello!"
```

<!--- SUFFIX
}
-->
<!--- KNIT example-gemini-12.kt -->

Check for examples in
the [integration tests](https://github.com/mokksy/ai-mocks/tree/main/ai-mocks-gemini/src/jvmTest/kotlin/me/kpavlov/aimocks/gemini).
