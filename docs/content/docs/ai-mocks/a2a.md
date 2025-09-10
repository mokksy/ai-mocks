---
title: "Agent2Agent Protocol"
#weight: 50
toc: true
---

[MockAgentServer](https://github.com/mokksy/ai-mocks/blob/main/ai-mocks-a2a/src/commonMain/kotlin/me/kpavlov/aimocks/a2a/MockAgentServer.kt) provides a local mock server for simulating [A2A (Agent-to-Agent) API](https://a2a-protocol.org/latest/specification/) endpoints.
It simplifies testing by allowing you to define request expectations and responses without making real network calls.

**NB!** The server only supports [JSON-RPC 2.0 transport](https://a2a-protocol.org/latest/specification/#321-json-rpc-20-transport).
Supported A2A protocol version is **0.3.0**.

## Quick Start

### Add Dependency

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

### Initialize the Server

```kotlin
val a2aServer = MockAgentServer(verbose = true)
```

- The server will start on a random free port by default.
- You can retrieve the server's base URL via `a2aServer.baseUrl()`.

## HTTP Client Setup

You may use any HTTP client that supports Server-Sent Events (SSE) to make requests to the mock server. The AI-Mocks A2A
library provides a convenient function to create a Ktor client configured for A2A:

```kotlin
// Create a Ktor client configured for A2A
val a2aClient = createA2AClient(url = a2aServer.baseUrl())
```

Alternatively, you can create the client manually:

```kotlin
// Create a Ktor client configured for A2A
val a2aClient = HttpClient(Java) {
    val json = Json {
        prettyPrint = true
        isLenient = true
    }
    install(ContentNegotiation) {
        json(json)
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

The [Agent Card endpoint](https://a2a-protocol.org/latest/specification/#55-agentcard-object-structure) provides information about the agent's capabilities, skills, and authentication mechanisms. Remote Agents that support A2A are required to publish an **Agent Card** in JSON format describing the agent's capabilities/skills and authentication mechanism. Clients use the Agent Card information to identify the best agent that can perform a task and leverage A2A to communicate with that remote agent.

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
    capabilities {
        streaming = true
        pushNotifications = true
        stateTransitionHistory = true
    }
    skills += skill {
        id = "walk"
        name = "Walk the walk"
      description = "I can walk"
      tags = listOf("move")
    }
    skills += skill {
        id = "talk"
        name = "Talk the talk"
      description = "I can talk"
      tags = listOf("communicate")
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
  .get("/.well-known/agent-card.json") {
    }.call
    .response
    .body<String>()

// Parse the response into an AgentCard object
val receivedCard = Json.decodeFromString<AgentCard>(response)
```

## Get Task Endpoint

The [Get Task endpoint](https://a2a-protocol.org/latest/specification/#73-tasksget) allows clients to retrieve information about a specific task. Clients may use this method to retrieve the generated Artifacts for a Task. The agent determines the retention window for Tasks previously submitted to it. The client may also request the last N items of history of the Task which will include all Messages, in order, sent by client and server.

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
      contextId = "ctx_12345"
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

## Send Message Endpoint

The [Send Message endpoint](https://a2a-protocol.org/latest/specification/#71-messagesend) allows clients to send a message to the agent for processing. This method allows a client to send content to a remote agent to start a new Task, resume an interrupted Task or reopen a completed Task. A Task interrupt may be caused due to an agent requiring additional user input or a runtime error.

Mock Server configuration:

```kotlin
// Create a Task object
val task = Task.create {
  id = "tid_12345"
  contextId = "ctx_12345"
  status {
    state = "completed"
  }
  artifact {
    name = "joke"
    parts += text { "This is a joke" }
    parts += file { uri = "https://example.com/readme.md" }
    parts += file { bytes = "1234".toByteArray() }
    parts += data { mapOf("foo" to "bar") }
  }
}

// Configure the mock server to respond with the task
a2aServer.sendMessage() responds {
  id = 1
  result = task
}
```

Client call example:

```kotlin
// Create a SendMessageRequest object using the builder function
val jsonRpcRequest = sendMessageRequest {
    id = "1"
    params {
        message {
            role = Message.Role.user
            parts += text { "Tell me a joke" }
            parts += file { uri = "https://example.com/readme.md" }
            parts += file { bytes = "1234".toByteArray() }
            parts += data { mapOf("foo" to "bar") }
        }
    }
}

// Make a POST request to the Send Message endpoint
val response = a2aClient
    .post("/") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToString(jsonRpcRequest))
    }.call
    .response

// Parse the response into a SendMessageResponse object
val body = response.body<String>()
val payload = Json.decodeFromString<SendMessageResponse>(body)
```

## Send Message Streaming Endpoint

The [Send Message Streaming endpoint](https://a2a-protocol.org/latest/specification/#72-messagestream) allows clients to send a message to the agent for processing and receive streaming updates. For clients and remote agents capable of communicating over HTTP with Server-Sent Events (SSE), clients can send the RPC request with method `message/stream` when creating a new Task. The remote agent can respond with a stream of TaskStatusUpdateEvents (to communicate status changes or instructions/requests) and TaskArtifactUpdateEvents (to stream generated results).

Mock Server configuration:

```kotlin
// Configure the mock server to respond with streaming updates
val taskId = "task_12345"

a2aServer.sendMessageStreaming() responds {
    delayBetweenChunks = 1.seconds
    responseFlow = flow {
      emit(
        taskStatusUpdateEvent {
          id = taskId
          status {
            state = "working"
            timestamp = Clock.System.now()
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
        }
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
        }
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
        }
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
        }
      )
      emit(
        taskStatusUpdateEvent {
          id = taskId
          status {
            state = "completed"
            timestamp = Clock.System.now()
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

// Make a POST request to the Send Message Streaming endpoint with SSE
a2aClient.sse(
    request = {
        url { a2aServer.baseUrl() }
        method = HttpMethod.Post
        val payload = SendStreamingMessageRequest(
            id = "1",
            params = MessageSendParams.create {
                message {
                    role = Message.Role.user
                    parts += textPart {
                        text = "Tell me a joke"
                    }
                }
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

The [Cancel Task endpoint](https://a2a-protocol.org/latest/specification/#74-taskscancel) allows clients to cancel a task that is in progress. A client may choose to cancel previously submitted Tasks, for example when the user no longer needs the result or wants to stop a long-running task.

Mock Server configuration:

```kotlin
// Configure the mock server to respond with a canceled task
a2aServer.cancelTask() responds {
    id = 1
    result {
        id = "tid_12345"
        sessionId = UUID.randomUUID().toString()
        status = TaskStatus(state = "canceled")
      contextId = UUID.randomUUID().toString()
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

## Set Task Push Notification Config Endpoint

The [Set Task Push Notification endpoint](https://a2a-protocol.org/latest/specification/#75-taskspushnotificationconfigset) allows clients to configure push notifications for a task. Clients may configure a push notification URL for receiving updates on Task status changes. This is particularly useful for long-running tasks where the client may not want to maintain an open connection.

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

## Get Task Push Notification Config Endpoint

The [Get Task Push Notification endpoint](https://a2a-protocol.org/latest/specification/#76-taskspushnotificationconfigget) allows clients to retrieve the push notification configuration for a specific task. Clients may retrieve the currently configured push notification configuration for a Task using this method, which is useful for verifying or displaying the current notification settings.

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

## List Task Push Notification Config Endpoint

The [List Task Push Notification Config endpoint](https://a2a-protocol.org/latest/specification/#77-taskspushnotificationconfiglist)
allows clients to list configured push notification destinations. This can be useful to inspect or manage existing
configurations.

Mock Server configuration:

```kotlin
// Configure the mock server to respond with a list of push notification configs
val taskId: TaskId = "task_12345"

a2aServer.listTaskPushNotificationConfig() responds {
  id = 1
  result = listOf(
    TaskPushNotificationConfig.create {
      id = taskId
      pushNotificationConfig {
        url = "https://example.com/callback"
        token = "abc.def.jk"
        authentication {
          schemes += "Bearer"
        }
      }
    }
  )
}
```

Client call example:

```kotlin
// Build a ListTaskPushNotificationConfigRequest
val jsonRpcRequest = ListTaskPushNotificationConfigRequest(
  id = "1",
  params = ListTaskPushNotificationConfigParams.create {
    limit(10)
    offset(0)
  },
)

// Make a POST request to the List Task Push Notification Config endpoint
val response = a2aClient
  .post("/") {
    contentType(ContentType.Application.Json)
    setBody(Json.encodeToString(jsonRpcRequest))
  }.call
  .response

// Parse the response
val body = response.body<String>()
val payload = Json.decodeFromString<ListTaskPushNotificationConfigResponse>(body)
```

## Delete Task Push Notification Config Endpoint

The [Delete Task Push Notification Config endpoint](https://a2a-protocol.org/latest/specification/#78-taskspushnotificationconfigdelete)
allows clients to delete the configured push notification destination for a task.

Mock Server configuration:

```kotlin
// Configure the mock server to respond to delete push notification config
val taskId: TaskId = "task_12345"

a2aServer.deleteTaskPushNotificationConfig() responds {
  id = 1
  // success without error
}
```

Client call example:

```kotlin
// Build a DeleteTaskPushNotificationConfigRequest
val jsonRpcRequest = DeleteTaskPushNotificationConfigRequest(
  id = "1",
  params = deleteTaskPushNotificationConfigParams {
    id(taskId)
  },
)

// Make a POST request to the Delete Task Push Notification Config endpoint
val response = a2aClient
  .post("/") {
    contentType(ContentType.Application.Json)
    setBody(Json.encodeToString(jsonRpcRequest))
  }.call
  .response

// Parse the response
val body = response.body<String>()
val payload = Json.decodeFromString<DeleteTaskPushNotificationConfigResponse>(body)
```

## Task Resubscription Endpoint

The [Task Resubscription endpoint](https://a2a-protocol.org/latest/specification/#79-tasksresubscribe) allows clients to resubscribe to streaming updates for a task that was previously created. This is useful when a client loses connection and needs to resume receiving updates for an ongoing task. A disconnected client may resubscribe to a remote agent that supports streaming to receive Task updates via Server-Sent Events (SSE).

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
                  timestamp = Clock.System.now()
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
                  timestamp = Clock.System.now()
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
a2aServer.sendPushNotification(event = taskUpdateEvent)
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
