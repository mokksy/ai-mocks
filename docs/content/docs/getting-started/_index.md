---
title: "Getting Started"
weight: 10
---

# Getting Started with Mokksy and AI-Mocks

This guide will help you get started with using Mokksy and AI-Mocks in your projects.

## Installation

### Gradle

Add the following dependencies to your `build.gradle.kts` file:

```kotlin
// For the core Mokksy HTTP mock server
implementation("me.kpavlov.aimocks:mokksy:latest.release")

// For mocking OpenAI API
implementation("me.kpavlov.aimocks:ai-mocks-openai:latest.release")

// For mocking Anthropic API
implementation("me.kpavlov.aimocks:ai-mocks-anthropic:latest.release")
```

### Maven

Add the following dependencies to your `pom.xml` file:

For the core Mokksy HTTP mock server:
```xml
<dependency>
  <groupId>me.kpavlov.aimocks</groupId>
  <artifactId>mokksy</artifactId>
  <version>latest.release</version>
</dependency>
```

For mocking OpenAI API:
```
<dependency>
  <groupId>me.kpavlov.aimocks</groupId>
  <artifactId>ai-mocks-openai</artifactId>
  <version>latest.release</version>
</dependency>
```

For mocking Anthropic API:
```
<dependency>
  <groupId>me.kpavlov.aimocks</groupId>
  <artifactId>ai-mocks-anthropic</artifactId>
  <version>latest.release</version>
</dependency>
```

## Basic Usage

### Using Mokksy

```kotlin
// Create a Mokksy instance
val mokksy = Mokksy()

// Configure a response for a GET request
mokksy.get {
  path = beEqual("/ping")
} respondsWith {
  body = """{"response": "Pong"}"""
}

// Start the server
mokksy.start()

// Use the server URL in your client
val serverUrl = mokksy.serverUrl

// Stop the server when done
mokksy.stop()
```

### Using AI-Mocks OpenAI

```kotlin
// Create a MockOpenAI instance
val mockOpenAI = MockOpenAI()

// Configure a response for a chat completion request
mockOpenAI.mockChatCompletion {
  request {
    messages {
      user("Hello")
    }
  }
  response {
    choices {
      choice {
        message {
          role = "assistant"
          content = "Hi there! How can I help you today?"
        }
      }
    }
  }
}

// Start the server
mockOpenAI.start()

// Use the server URL in your OpenAI client
val serverUrl = mockOpenAI.serverUrl

// Stop the server when done
mockOpenAI.stop()
```

For more detailed examples and usage instructions, see the specific documentation for each component:

- [Mokksy](../mokksy/)
- [AI-Mocks OpenAI](../ai-mocks-openai/)
- [AI-Mocks Anthropic](../ai-mocks-anthropic/)
