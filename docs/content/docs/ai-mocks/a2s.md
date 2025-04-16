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

```kotlin
// Configure the mock server to respond with the AgentCard
a2aServer.agentCard() responds {
    delay = 1.milliseconds
    card {
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
}
```

## Get Task Endpoint

The Get Task endpoint allows clients to retrieve information about a specific task.

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

## Send Task Endpoint

The Send Task endpoint allows clients to send a task to the agent for processing.

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

## Making Requests to the Mock Server

You can use any HTTP client to make requests to the mock server. Here's an example using Ktor:

```kotlin
// Create a Ktor client
val a2aClient = HttpClient(Java) {
    install(ContentNegotiation) {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
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

// Make a request to the Agent Card endpoint
val receivedCard = a2aClient
    .get("/.well-known/agent.json")
    .body<AgentCard>()
```

## Verifying Requests

After your test is complete, you can verify that all expected requests were received:

```kotlin
a2aServer.verifyNoUnmatchedRequests()
```

This ensures that your test made all the expected requests to the mock server.
