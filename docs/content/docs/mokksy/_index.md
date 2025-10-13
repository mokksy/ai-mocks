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
testImplementation("dev.mokksy.mokksy.mokksy-jvm:$latestVersion")

// For Kotlin Multiplatform projects
testImplementation("dev.mokksy.mokksy.mokksy:$latestVersion")
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
val serverUrl = mokksy.baseUrl

// [create a client and send a request here]

// Shutdown Mokksy when done
mokksy.shutdown()
```

This snippet shows how to use the Mokksy server for testing.  
It starts and configures a server so that any HTTP GET request to `/ping` returns `{"response": "Pong"}`.  
It also retrieves the server’s base URL for client requests and demonstrates how to shut down the server after testing.

## Responding with Predefined Responses

### GET Request

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

## Verifying Requests

After your test is complete, you can verify that all expected requests were received:

```kotlin
// Verify no unmatched requests
mokksy.checkForUnmatchedRequests()
```

If there is an unexpected request to the mock, then AssertionError will be thrown.

It's a good practice to run it after every test:

```kotlin
class MyTest {
    
    private val mokksy = Mokksy()
  
    @AfterEach
    fun afterEach() {
      mokksy.checkForUnmatchedRequests()
    }
  
    @Test
    fun testSomething() {
        TODO("Write your test here")
    }
}
```
