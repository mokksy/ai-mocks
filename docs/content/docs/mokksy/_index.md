---
title: "Mokksy"
weight: 20
---

_Mokksy_ is a mock HTTP server built with [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/).

![Mokksy Mascot](../../mokksy-mascot-256.png)

**Why?** Wiremock does not support true SSE and streaming responses. Mokksy is here to address those limitations. It's particularly useful for integration testing LLM clients.

## Core Features

- Flexibility to control server response directly via `ApplicationCall` object
- Built with [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html)
- Fluent modern Kotlin DSL API
- Support for simulating streamed responses and [Server-Side Events (SSE)](https://html.spec.whatwg.org/multipage/server-sent-events.html)

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
