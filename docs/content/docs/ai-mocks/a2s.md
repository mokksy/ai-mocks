---
title: "Agent2Agent Protocol"
#weight: 50
toc: true
---

[MockAgentServer](https://github.com/kpavlov/ai-mocks/blob/main/ai-mocks-a2a/src/commonMain/kotlin/me/kpavlov/aimocks/a2a/MockAgentServer.kt) provides a local mock server for simulating [A2A (Agent-to-Agent) API endpoints](https://google.github.io/A2A/). It simplifies testing by allowing you to define request expectations and responses without making real network calls.

## Quick Start

1. **Add Dependency**
   Include the library in your test dependencies (Maven or Gradle).

    {{< tabs "dependencies" >}}
    {{< tab "Gradle" >}}
```kotlin
implementation("me.kpavlov.aimocks:ai-mocks-a2a-jvm:$latestVersion")
```
    {{< /tab >}}
    {{< tab "Maven" >}}
```xml
<dependency>
    <groupId>me.kpavlov.aimocks</groupId>
    <artifactId>ai-mocks-a2a-jvm</artifactId>
    <version>[LATEST_VERSION]</version>
</dependency>
```
    {{< /tab >}}
    {{< /tabs >}}


2. **Initialize the Server**
   ```kotlin
   val a2aServer = MockAgentServer(verbose = true)
   ```
  - The server will start on a random free port by default.
  - You can retrieve the server's base URL via `a2aServer.baseUrl()`.

3. **Configure Requests and Responses**

## Agent Card Endpoint

The Agent Card endpoint provides information about the agent's capabilities, skills, and authentication mechanisms.

### Server Configuration

```kotlin
// Create an AgentCard object
val agentCard = AgentCard.create {
    name = "test-agent"
    description = "test-agent-description"
    url = a2aServer.baseUrl()
    documentationUrl = "https://example.com/documentation"
    version = "0.0.1"
    provider = AgentProvider(
        "Acme, Inc.",
        "https://example.com/organization",
    )
    authentication = AgentAuthentication(
        schemes = listOf("none", "bearer"),
        credentials = "test-token",
    )
    capabilities = AgentCapabilities(
        streaming = true,
        pushNotifications = true,
        stateTransitionHistory = true,
    )
    skills = listOf(
        AgentSkill(
            id = "walk",
            name = "Walk the walk",
        ),
        AgentSkill(
            id = "talk",
            name = "Talk the talk",
        ),
    )
}

// Configure the mock server to respond with the AgentCard
a2aServer.agentCard() responds {
    delay = 1.milliseconds
    card = agentCard
}
```

### Client Example

```kotlin
// Make a GET request to the Agent Card endpoint
val response = a2aClient
    .get("/.well-known/agent.json") {
    }.call
    .response
    .body<String>()

// Parse the response into an AgentCard object
val receivedCard = Json.decodeFromString<AgentCard>(response)
```

## Get Task Endpoint

The Get Task endpoint allows clients to retrieve information about a specific task.

### Server Configuration

```kotlin
// Configure the mock server to respond with a task
a2aServer.getTask() responds {
    id = 1
    result {
        id = "tid_12345"
        sessionId = null
        status = TaskStatus(state = "completed")
        artifacts = listOf(
            Artifact(
                name = "joke",
                parts = listOf(
                    TextPart(
                        text = "This is a joke",
                    ),
                ),
            ),
        )
    }
}
```

### Client Example

```kotlin
// Create a GetTaskRequest object
val jsonRpcRequest = GetTaskRequest(
    id = "1",
    params = TaskQueryParams(
        id = UUID.randomUUID().toString(),
        historyLength = 2,
    ),
)

// Make a POST request to the Get Task endpoint
val response = a2aClient
    .post("/") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(jsonRpcRequest))
    }.call
    .response

// Parse the response into a GetTaskResponse object
val body = response.body<String>()
val payload = Json.decodeFromString<GetTaskResponse>(body)
```

## Send Task Endpoint

The Send Task endpoint allows clients to send a task to the agent for processing.

### Server Configuration

```kotlin
// Create a Task object
val task = Task(
    id = "tid_12345",
    sessionId = null,
    status = TaskStatus(state = "completed"),
    artifacts = listOf(
        Artifact(
            name = "joke",
            parts = listOf(
                TextPart(
                    text = "This is a joke",
                ),
            ),
        ),
    ),
)

// Configure the mock server to respond with the task
a2aServer.sendTask() responds {
    id = 1
    result = task
}
```

### Client Example

```kotlin
// Create a SendTaskRequest object
val jsonRpcRequest = SendTaskRequest(
    id = "1",
    params = TaskSendParams(
        id = UUID.randomUUID().toString(),
        message = Message(
            role = Message.Role.user,
            parts = listOf(
                TextPart(
                    text = "Tell me a joke",
                ),
            ),
        ),
    ),
)

// Make a POST request to the Send Task endpoint
val response = a2aClient
    .post("/") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(jsonRpcRequest))
    }.call
    .response

// Parse the response into a SendTaskResponse object
val body = response.body<String>()
val payload = Json.decodeFromString<SendTaskResponse>(body)
```

## Send Task Streaming Endpoint

The Send Task Streaming endpoint allows clients to send a task to the agent for processing and receive streaming updates.

### Server Configuration

```kotlin
// Configure the mock server to respond with streaming updates
val taskId = "task_12345"

a2aServer.sendTaskStreaming() responds {
    delayBetweenChunks = 1.seconds
    responseFlow = flow {
        emit(
            TaskStatusUpdateEvent(
                id = taskId,
                status = TaskStatus(state = "working", timestamp = System.now()),
            ),
        )
        emit(
            TaskArtifactUpdateEvent(
                id = taskId,
                artifact = Artifact(
                    name = "joke",
                    parts = listOf(
                        TextPart(
                            text = "This",
                        ),
                    ),
                    append = false,
                ),
            ),
        )
        emit(
            TaskArtifactUpdateEvent(
                id = taskId,
                artifact = Artifact(
                    name = "joke",
                    parts = listOf(
                        TextPart(
                            text = "is",
                        ),
                    ),
                    append = false,
                ),
            ),
        )
        emit(
            TaskArtifactUpdateEvent(
                id = taskId,
                artifact = Artifact(
                    name = "joke",
                    parts = listOf(
                        TextPart(
                            text = "a",
                        ),
                    ),
                    append = false,
                ),
            ),
        )
        emit(
            TaskArtifactUpdateEvent(
                id = taskId,
                artifact = Artifact(
                    name = "joke",
                    parts = listOf(
                        TextPart(
                            text = "joke!",
                        ),
                    ),
                    append = false,
                    lastChunk = true,
                ),
            ),
        )
        emit(
            TaskStatusUpdateEvent(
                id = taskId,
                status = TaskStatus(state = "completed", timestamp = System.now()),
                final = true,
            ),
        )
    }
}
```

### Client Example

```kotlin
// Create a collection to store the events
var collectedEvents = ConcurrentLinkedQueue<TaskUpdateEvent>()

// Make a POST request to the Send Task Streaming endpoint with SSE
a2aClient.sse(
    request = {
        url { a2aServer.baseUrl() }
        method = HttpMethod.Post
        val payload = SendTaskStreamingRequest(
            id = "1",
            params = TaskSendParams(
                id = UUID.randomUUID().toString(),
                message = Message(
                    role = Message.Role.user,
                    parts = listOf(
                        TextPart(
                            text = "Tell me a joke",
                        ),
                    ),
                ),
            ),
        )
        body = TextContent(
            text = Json.encodeToString(payload),
            contentType = ContentType.Application.Json,
        )
    },
) {
    var reading = true
    while (reading) {
        incoming.collect {
            println("Event from server:\n$it")
            it.data?.let {
                val event = Json.decodeFromString<TaskUpdateEvent>(it)
                collectedEvents.add(event)
                // Process the event
                when (event) {
                    is TaskStatusUpdateEvent -> {
                        println("Task status: $event")
                        if (event.final) {
                            reading = false
                            cancel("Finished")
                        }
                    }
                    is TaskArtifactUpdateEvent -> {
                        println("Task artifact: $event")
                    }
                }
            }
        }
    }
}
```

## Cancel Task Endpoint

The Cancel Task endpoint allows clients to cancel a task that is in progress.

### Server Configuration

```kotlin
// Configure the mock server to respond with a canceled task
a2aServer.cancelTask() responds {
    id = 1
    result {
        id = "tid_12345"
        sessionId = UUID.randomUUID().toString()
        status = TaskStatus(state = "canceled")
    }
}
```

### Client Example

```kotlin
// Create a CancelTaskRequest object
val jsonRpcRequest = CancelTaskRequest(
    id = "1",
    params = TaskIdParams(
        id = UUID.randomUUID().toString(),
    ),
)

// Make a POST request to the Cancel Task endpoint
val response = a2aClient
    .post("/") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(jsonRpcRequest))
    }.call
    .response

// Parse the response into a CancelTaskResponse object
val body = response.body<String>()
val payload = Json.decodeFromString<CancelTaskResponse>(body)
```

## Set Task Push Notification Endpoint

The Set Task Push Notification endpoint allows clients to configure push notifications for a task.

### Server Configuration

```kotlin
// Create a TaskPushNotificationConfig object
val config = TaskPushNotificationConfig(
    id = "task_12345",
    pushNotificationConfig = PushNotificationConfig(
        url = "https://example.com/callback",
        token = "abc.def.jk",
        authentication = AuthenticationInfo(
            schemes = listOf("Bearer"),
        ),
    ),
)

// Configure the mock server to respond with the config
a2aServer.setTaskPushNotification() responds {
    id = 1
    result = config
}
```

### Client Example

```kotlin
// Create a TaskPushNotificationConfig object
val config = TaskPushNotificationConfig(
    id = "task_12345",
    pushNotificationConfig = PushNotificationConfig(
        url = "https://example.com/callback",
        token = "abc.def.jk",
        authentication = AuthenticationInfo(
            schemes = listOf("Bearer"),
        ),
    ),
)

// Create a SetTaskPushNotificationRequest object
val jsonRpcRequest = SetTaskPushNotificationRequest(
    id = "1",
    params = config,
)

// Make a POST request to the Set Task Push Notification endpoint
val response = a2aClient
    .post("/") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(jsonRpcRequest))
    }.call
    .response

// Parse the response into a SetTaskPushNotificationResponse object
val body = response.body<String>()
val payload = Json.decodeFromString<SetTaskPushNotificationResponse>(body)
```

## HTTP Client Setup

You can use any HTTP client to make requests to the mock server. Here's how to create a Ktor client for A2A:

```kotlin
// Create a Ktor client
val a2aClient = HttpClient(Java) {
    install(ContentNegotiation) {
        Json {
            prettyPrint = true
            isLenient = true
        }
    }
    install(SSE) {
        showRetryEvents()
        showCommentEvents()
    }
    install(DefaultRequest) {
        url(a2aServer.baseUrl()) // Set the base URL
    }
}
```

## Verifying Requests

After your test is complete, you can verify that all expected requests were received:

```kotlin
a2aServer.verifyNoUnmatchedRequests()
```

This ensures that your test made all the expected requests to the mock server.
