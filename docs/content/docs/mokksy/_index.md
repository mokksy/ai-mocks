---
title: Mokksy
weight: 20
#toc: true
#featureImage: "mokksy-mascot-256.png"
---

_Mokksy_ is a mock HTTP server built with [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

**Why?** Wiremock does not support true SSE and streaming responses. Mokksy is here to address those limitations. It's particularly useful for integration testing LLM clients.

## Core Features

- Flexibility to control server response directly via `ApplicationCall` object
- Built with [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html)
- Fluent modern Kotlin DSL API
- Support for simulating streamed responses and [Server-Side Events (SSE)](https://html.spec.whatwg.org/multipage/server-sent-events.html)

## Installation

Add Mokksy to your project dependencies:

{{< tabs "dependencies" >}}
{{< tab "Gradle" >}}

```kotlin
// For JVM projects
testImplementation("dev.mokksy:mokksy-jvm:$latestVersion")

// For Kotlin Multiplatform projects
testImplementation("dev.mokksy:mokksy:$latestVersion")
```

{{< /tab >}}
{{< tab "Maven" >}}

```xml

<dependency>
  <groupId>dev.mokksy</groupId>
  <artifactId>mokksy-jvm</artifactId>
  <version>[LATEST_VERSION]</version>
  <scope>test</scope>
</dependency>
```

{{< /tab >}}
{{< /tabs >}}

## Basic Usage

### Creating Mokksy Server

<!--- CLEAR -->
<!--- INCLUDE
import dev.mokksy.mokksy.Mokksy
import dev.mokksy.mokksy.shutdown
fun main() {
-->
```kotlin
// Create and start Mokksy instance
val mokksy = Mokksy()

// Configure a response for a GET request
mokksy.get {
  path("/ping")
} respondsWith {
  // language=json
  body = """{"response": "Pong"}"""
}

// Use the server URL in your client
val serverUrl = mokksy.baseUrl()

// [create a client and send a request here]

// Shutdown Mokksy when done
mokksy.shutdown()
```

<!--- SUFFIX
}
-->
<!--- KNIT example-mokksy-docs-01.kt -->

This snippet shows how to use the Mokksy server for testing.  
It starts and configures a server so that any HTTP GET request to `/ping` returns `{"response": "Pong"}`.  
It also retrieves the server’s base URL for client requests and demonstrates how to shut down the server after testing.

## Responding with Predefined Responses

### GET Request

<!--- INCLUDE
import dev.mokksy.mokksy.Mokksy
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
val mokksy = Mokksy()
val client = HttpClient {
    install(DefaultRequest) {
        url(mokksy.baseUrl())
    }
}
fun main() = runBlocking {
-->
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
result.status shouldBe HttpStatusCode.OK
result.bodyAsText() shouldBe expectedResponse
```

<!--- SUFFIX
}
-->
<!--- KNIT example-mokksy-docs-02.kt -->

### POST Request

<!--- INCLUDE
import dev.mokksy.mokksy.Mokksy
import io.kotest.matchers.equals.beEqual
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
val mokksy = Mokksy()
val client = HttpClient {
    install(DefaultRequest) {
        url(mokksy.baseUrl())
    }
}
fun main() = runBlocking {
-->
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

<!--- SUFFIX
}
-->
<!--- KNIT example-mokksy-docs-03.kt -->

### Request Specification Matchers

Mokksy provides various matcher types to specify conditions for matching incoming HTTP requests:

* **Path Matchers**
  * `path` - Exact match for request path
  * Example: `path("/things")`

* **Content Matchers**
  * `bodyContains` - Checks if the body contains specific text
  * Example: `bodyContains("value")` or `bodyString += contain("value")`

* **Header Matchers**
  * `containsHeader` - Checks if the request contains a specific header with value
  * Example: `containsHeader("X-Request-ID", "RequestID")`

* **Predicate Matchers**
  * `predicateMatcher` - Custom predicate function to match against request body
  * Example: `bodyMatchesPredicate { it?.name == "foo" }`

* **Call Matchers**
  * `successCallMatcher` - Matches if a function call with the request body succeeds
  * Example: `requestSatisfies { input -> input.shouldNotBeNull() }`

## Server-Side Events (SSE) Response

[Server-Side Events (SSE)](https://html.spec.whatwg.org/multipage/server-sent-events.html) is a technology that allows a server to push updates to the client over a single, long-lived HTTP connection. This enables real-time updates without requiring the client to continuously poll the server for new data.

SSE streams events in a standardized format, making it easy for clients to consume the data and handle events as they arrive. It's lightweight and efficient, particularly well-suited for applications requiring real-time updates like live notifications or feed updates.

<!--- INCLUDE
import dev.mokksy.mokksy.Mokksy
import io.kotest.matchers.equals.beEqual
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import org.assertj.core.api.Assertions.assertThat
val mokksy = Mokksy()
val client = HttpClient {
    install(DefaultRequest) {
        url(mokksy.baseUrl())
    }
}
fun main() = runBlocking {
-->
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
    .isEqualTo(ContentType.Text.EventStream.withCharset(Charsets.UTF_8))
assertThat(result.bodyAsText())
    .isEqualTo("data: One\r\ndata: Two\r\n")
```

<!--- SUFFIX
}
-->
<!--- KNIT example-mokksy-docs-04.kt -->

## Verifying Requests

Mokksy provides two complementary verification methods that check opposite sides of the stub/request contract.

### Verify all stubs were triggered

`verifyNoUnmatchedStubs()` fails if any registered stub was never matched by an incoming request. Use this to catch
stubs you set up but that were never actually called — a sign the code under test took a different path than expected.

<!--- INCLUDE
import dev.mokksy.mokksy.Mokksy
val mokksy = Mokksy()
fun main() {
-->
```kotlin
// Fails if any stub has matchCount == 0
mokksy.verifyNoUnmatchedStubs()
```
Be careful if you are running tests in parallel against a single MokksyServer instance.
Some stubs might be unmatched after one test is completed. Don't verify in `@AfterEach`/`@AfterTest`.

### Verify no unexpected requests arrived

`verifyNoUnexpectedRequests()` fails if any HTTP request arrived at the server but no stub matched it. These requests are
recorded in the `RequestJournal` and reported together.

```kotlin
// Fails if any request arrived with no matching stub (unexpected request)
mokksy.verifyNoUnexpectedRequests()
```

<!--- SUFFIX
}
-->
<!--- KNIT example-mokksy-docs-05.kt -->

If there is an unexpected request to the mock, then `AssertionError` will be thrown.

### Recommended AfterEach setup

Run both checks after every test to catch a mismatch in either direction:

<!--- INCLUDE
import dev.mokksy.mokksy.Mokksy
import kotlin.test.AfterTest
import kotlin.test.Test
-->
```kotlin
class MyTest {

    private val mokksy = Mokksy()

    @AfterTest
    fun afterEach() {
      mokksy.verifyNoUnexpectedRequests() // no unexpected HTTP calls
    }

    @Test
    fun testSomething() {
        // write your test here
    }
}
```
<!--- KNIT example-mokksy-docs-06.kt -->


### Inspecting unmatched items

Use the `find*` variants to retrieve the unmatched items directly for custom assertions:

```kotlin
// List<RecordedRequest> — HTTP requests with no matching stub
val unmatchedRequests: List<RecordedRequest> = mokksy.findAllUnmatchedRequests()

// List<RequestSpecification<*>> — stubs that were never triggered
val unmatchedStubs: List<RequestSpecification<*>> = mokksy.findAllUnmatchedStubs()
```

`RecordedRequest` is an immutable snapshot that captures the `method`, `uri`, and `headers` of the incoming request.

## Request Journal

Mokksy records incoming requests in a `RequestJournal`. The recording mode is controlled by `JournalMode` in
`ServerConfiguration`:

| Mode                           | Behaviour                                                                                                  |
|--------------------------------|------------------------------------------------------------------------------------------------------------|
| `JournalMode.LEAN` *(default)* | Records only requests with no matching stub. Lower overhead. Sufficient for `checkForUnmatchedRequests()`. |
| `JournalMode.FULL`             | Records all incoming requests — both matched and unmatched.                                                |

```kotlin
val mokksy = Mokksy(
  configuration = ServerConfiguration(
    journalMode = JournalMode.FULL
  )
)
```

Use `FULL` mode when you need a complete picture of traffic, for example to debug unexpected call patterns by combining
both lists:

```kotlin
val unmatched = mokksy.findAllUnmatchedRequests()
// unmatched is empty only if every request found a matching stub
```

Call `resetMatchCounts()` between tests to clear both stub match counts and the journal:

```kotlin
@AfterTest
fun afterEach() {
  mokksy.resetMatchCounts()
}
```
