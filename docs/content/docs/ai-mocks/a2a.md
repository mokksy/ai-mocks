---
title: "Agent2Agent Protocol"
#weight: 50
toc: true
---

[MockAgentServer](https://github.com/mokksy/ai-mocks/blob/main/ai-mocks-a2a/src/commonMain/kotlin/me/kpavlov/aimocks/a2a/MockAgentServer.kt) provides a local mock server for simulating [A2A (Agent-to-Agent) API endpoints](https://google.github.io/A2A/). It simplifies testing by allowing you to define request expectations and responses without making real network calls.

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

## HTTP Client Setup

You may use any HTTP client which supports Server-Sent Events (SSE) to make requests to the mock server. Here's how to create a Ktor client for A2A:

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

## Agent Card Endpoint

The Agent Card endpoint provides information about the agent's capabilities, skills, and authentication mechanisms. Remote Agents that support A2A are required to publish an **Agent Card** in JSON format describing the agent's capabilities/skills and authentication mechanism. Clients use the Agent Card information to identify the best agent that can perform a task and leverage A2A to communicate with that remote agent.

Mock Server configuration:
```kotlin
// Create an AgentCard object
val agentCard = AgentCard.create {
    name = "test-agent"
    description = "test-agent-description"
    url = a2aServer.baseUrl()
    documentationUrl = "https://example.com/documentation"
    version = "0.0.1"
    provider {
        organization = "Acme, Inc."
        url = "https://example.com/organization"
    }
    authentication {
        schemes = listOf("none", "bearer")
        credentials = "test-token"
    }
    capabilities {
        streaming = true
        pushNotifications = true
        stateTransitionHistory = true
    }
    skills += skill {
        id = "walk"
        name = "Walk the walk"
      }
    skills += skill {
        id = "talk"
        name = "Talk the talk"
    }
}

// Configure the mock server to respond with the AgentCard
a2aServer.agentCard() responds {
    delay = 1.milliseconds
    card = agentCard
}
```

Client call example:

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

The Get Task endpoint allows clients to retrieve information about a specific task. Clients may use this method to retrieve the generated Artifacts for a Task. The agent determines the retention window for Tasks previously submitted to it. The client may also request the last N items of history of the Task which will include all Messages, in order, sent by client and server.

Mock Server configuration:

```kotlin
// Configure the mock server to respond with a task
a2aServer.getTask() responds {
    id = 1
    result {
        id = "tid_12345"
        sessionId = "de38c76d-d54c-436c-8b9f-4c2703648d64"
        status {
            state = "completed"
        }
        artifacts += artifact {
            name = "joke"
            parts += textPart {
                text = "This is a joke"
            }
        }
    } 
}
```

You can also configure the mock server to respond with an error:

```kotlin
// Configure the mock server to respond with a task not found error
a2aServer.getTask() responds {
    id = 1
    error = taskNotFoundError {
        message = "Task not found"
    }
}
```

Client call example:

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

The Send Task endpoint allows clients to send a task to the agent for processing. This method allows a client to send content to a remote agent to start a new Task, resume an interrupted Task or reopen a completed Task. A Task interrupt may be caused due to an agent requiring additional user input or a runtime error.

Mock Server configuration:

```kotlin
// Create a Task object
val task = Task.create {
  id("tid_12345")
  status {
    state("completed")
  }
  artifacts += artifact {
    name = "joke"
    parts += text { "This is a joke" }
    parts += file { uri = "https://example.com/readme.md" }
    parts += file { bytes = "1234".toByteArray() }
    parts += data { mapOf("foo" to "bar") }
  }
}

// Configure the mock server to respond with the task
a2aServer.sendTask() responds {
  id = 1
  result = task
}
```

Client call example:

```kotlin
// Create a SendTaskRequest object using the builder function
val jsonRpcRequest = SendTaskRequest.create {
    id = "1"
    params {
        id = UUID.randomUUID().toString()
        message {
            role = Message.Role.user
            parts += textPart {
                text = "Tell me a joke"
            }
        }
    }
}

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

The Send Task Streaming endpoint allows clients to send a task to the agent for processing and receive streaming updates. For clients and remote agents capable of communicating over HTTP with Server-Sent Events (SSE), clients can send the RPC request with method `tasks/sendSubscribe` when creating a new Task. The remote agent can respond with a stream of TaskStatusUpdateEvents (to communicate status changes or instructions/requests) and TaskArtifactUpdateEvents (to stream generated results).

Mock Server configuration:

```kotlin
// Configure the mock server to respond with streaming updates
val taskId = "task_12345"

a2aServer.sendTaskStreaming() responds {
    delayBetweenChunks = 1.seconds
    responseFlow = flow {
      emit(
        taskStatusUpdateEvent {
          id = taskId
          status {
            state = "working"
            timestamp = System.now()
          }
        }
      )
      emit(
        taskArtifactUpdateEvent {
          id = taskId
          artifact {
            name = "joke"
            parts += textPart {
              text = "This"
            }
          }
        },
      )
      emit(
        taskArtifactUpdateEvent {
          id = taskId
          artifact {
            name = "joke"
            parts += textPart {
              text = "is"
            }
            append = true
          }
        },
      )
      emit(
        taskArtifactUpdateEvent {
          id = taskId
          artifact {
            name = "joke"
            parts += textPart {
              text = "a"
            }
            append = true
          }
        },
      )
      emit(
        taskArtifactUpdateEvent {
          id = taskId
          artifact {
            name = "joke"
            parts += textPart {
              text = "joke!"
            }
            append = true
            lastChunk = true
          }
        },
      )
      emit(
        taskStatusUpdateEvent {
          id = taskId
          status {
            state = "completed"
            timestamp = System.now()
          }
          final = true
        }
      )
    }
}
```

Client call example:

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
            params = TaskSendParams.create {
                id = UUID.randomUUID().toString()
                message = Message(
                    role = Message.Role.user,
                    parts = listOf(
                        TextPart(
                            text = "Tell me a joke",
                        ),
                    ),
                )
            },
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
                if (!handleEvent(event)) {
                    reading = false
                    cancel("Finished")
                }
            }
        }
    }
}

// Helper function to handle events
private fun handleEvent(event: TaskUpdateEvent): Boolean {
    when (event) {
        is TaskStatusUpdateEvent -> {
            println("Task status: $event")
            if (event.final) {
                return false
            }
        }
        is TaskArtifactUpdateEvent -> {
            println("Task artifact: $event")
        }
    }
    return true
}
```

## Cancel Task Endpoint

The Cancel Task endpoint allows clients to cancel a task that is in progress. A client may choose to cancel previously submitted Tasks, for example when the user no longer needs the result or wants to stop a long-running task.

Mock Server configuration:

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

Client call example:

```kotlin
// Create a CancelTaskRequest object
val jsonRpcRequest = cancelTaskRequest {
    id = "1"
    params {
        id = UUID.randomUUID().toString()
    }
}

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

The Set Task Push Notification endpoint allows clients to configure push notifications for a task. Clients may configure a push notification URL for receiving updates on Task status changes. This is particularly useful for long-running tasks where the client may not want to maintain an open connection.

Mock Server configuration:

```kotlin
// Create a TaskPushNotificationConfig object
val taskId: TaskId = "task_12345"
val config = TaskPushNotificationConfig.create {
    id = taskId
    pushNotificationConfig {
        url = "https://example.com/callback"
        token = "abc.def.jk"
        authentication {
            credentials = "secret"
            schemes += "Bearer"
        }
    }
}

// Configure the mock server to respond with the config
a2aServer.setTaskPushNotification() responds {
    id = 1
    result {
        id = taskId
        pushNotificationConfig {
            url = "https://example.com/callback"
            token = "abc.def.jk"
            authentication {
                credentials = "secret"
                schemes += "Bearer"
            }
        }
    }
}
```

Client call example:

```kotlin
// Create a TaskPushNotificationConfig object
val config = TaskPushNotificationConfig.create {
    id = "task_12345"
    pushNotificationConfig {
        url = "https://example.com/callback"
        token = "abc.def.jk"
        authentication {
            credentials = "secret"
            schemes += "Bearer"
        }
    }
}

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

## Get Task Push Notification Endpoint

The Get Task Push Notification endpoint allows clients to retrieve the push notification configuration for a specific task. Clients may retrieve the currently configured push notification configuration for a Task using this method, which is useful for verifying or displaying the current notification settings.

Mock Server configuration:

```kotlin
// Create a TaskPushNotificationConfig object
val taskId: TaskId = "task_12345"
val config = TaskPushNotificationConfig(
    id = taskId,
    pushNotificationConfig = PushNotificationConfig(
        url = "https://example.com/callback",
        token = "abc.def.jk",
        authentication = AuthenticationInfo(
            schemes = listOf("Bearer"),
        ),
    ),
)

// Configure the mock server to respond with the config
a2aServer.getTaskPushNotification() responds {
    id = 1
    result = config
}
```

Client call example:

```kotlin
// Create a GetTaskPushNotificationRequest object
val jsonRpcRequest = GetTaskPushNotificationRequest(
    id = "1",
    params = TaskIdParams(
        id = taskId,
    ),
)

// Make a POST request to the Get Task Push Notification endpoint
val response = a2aClient
    .post("/") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(jsonRpcRequest))
    }.call
    .response

// Parse the response into a GetTaskPushNotificationResponse object
val body = response.body<String>()
val payload = Json.decodeFromString<GetTaskPushNotificationResponse>(body)
```

## Task Resubscription Endpoint

The Task Resubscription endpoint allows clients to resubscribe to streaming updates for a task that was previously created. This is useful when a client loses connection and needs to resume receiving updates for an ongoing task. A disconnected client may resubscribe to a remote agent that supports streaming to receive Task updates via Server-Sent Events (SSE).

Mock Server configuration:

```kotlin
// Configure the mock server to respond with streaming updates
val taskId: TaskId = "task_12345"

a2aServer.taskResubscription() responds {
    delayBetweenChunks = 1.seconds
    responseFlow = flow {
        emit(
            taskStatusUpdateEvent {
                id = taskId
                status {
                    state = "working"
                    timestamp = System.now()
                }
            }
        )
        emit(
            taskArtifactUpdateEvent {
                id = taskId
                artifact {
                    name = "joke"
                    parts += textPart {
                        text = "This is a resubscribed joke!"
                    }
                    lastChunk = true
                }
            }
        )
        emit(
            taskStatusUpdateEvent {
                id = taskId
                status {
                    state = "completed"
                    timestamp = System.now()
                }
                final = true
            }
        )
    }
}
```

Client call example:

```kotlin
// Create a collection to store the events
val collectedEvents = ConcurrentLinkedQueue<TaskUpdateEvent>()

// Make a POST request to the Task Resubscription endpoint with SSE
a2aClient.sse(
    request = {
        url { a2aServer.baseUrl() }
        method = HttpMethod.Post
        contentType(ContentType.Application.Json)
        val payload = TaskResubscriptionRequest(
            id = "1",
            params = TaskQueryParams(
                id = taskId,
            ),
        )
        setBody(payload)
    },
) {
    var reading = true
    while (reading) {
        incoming.collect {
            println("Event from server:\n$it")
            it.data?.let {
                val event = Json.decodeFromString<TaskUpdateEvent>(it)
                collectedEvents.add(event)
                if (!handleEvent(event)) {
                    reading = false
                    cancel("Finished")
                }
            }
        }
    }
}

// Helper function to handle events
private fun handleEvent(event: TaskUpdateEvent): Boolean {
    when (event) {
        is TaskStatusUpdateEvent -> {
            println("Task status: $event")
            if (event.final) {
                return false
            }
        }
        is TaskArtifactUpdateEvent -> {
            println("Task artifact: $event")
        }
    }
    return true
}
```

## Testing Push Notifications

The A2A protocol supports push notifications, which allow agents to notify clients of updates outside a connected session. This is particularly useful for long-running tasks where the client may not want to maintain an open connection.

### Accessing Task Notification History

You can access the notification history for a specific task using the `getTaskNotifications` method:

```kotlin
val taskId: TaskId = "task_12345"
val notificationHistory = a2aServer.getTaskNotifications(taskId)

// Verify that the history is initially empty
notificationHistory.events() shouldHaveSize 0
```

### Sending Push Notifications

You can send push notifications using the `sendPushNotification` method:

```kotlin
val taskUpdateEvent = taskArtifactUpdateEvent {
    id = taskId
    artifact {
        name = "joke"
        parts += textPart {
            text = "This is a notification joke!"
        }
        lastChunk = true
    }
}
a2aServer.sendPushNotification(event=taskUpdateEvent)
```

### Verifying Notifications

You can verify that notifications were received by checking the notification history:

```kotlin
// Verify that the notification history contains the event
notificationHistory.events() shouldContain taskUpdateEvent
```

## Verifying Requests

After your test is complete, you can verify that all expected requests were received:

```kotlin
a2aServer.verifyNoUnmatchedRequests()
```

This ensures that your test made all the expected requests to the mock server.
