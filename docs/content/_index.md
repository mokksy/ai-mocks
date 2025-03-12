---
title: "Mokksy and AI-Mocks"
---
![Mokksy Mascot](mokksy-mascot-256.png)
# Mokksy and AI-Mocks

_Mokksy_ and _AI-Mocks_ are mock HTTP and LLM (Large Language Model) servers inspired by WireMock, with support for
response streaming and Server-Side Events (SSE). They are designed to build, test, and mock OpenAI API responses for
development purposes.

## Project Overview

Mokksy and AI-Mocks are a suite of tools designed for mocking HTTP and LLM (Large Language Model) APIs for testing and development purposes.

### Components

1. **Mokksy**
   - A mock HTTP server built with [Kotlin](https://kotlinlang.org/) and [Ktor](https://ktor.io/)
   - Supports true Server-Side Events (SSE) and streaming responses
   - Designed to overcome limitations in existing tools like WireMock
   - Provides a flexible, fluent Kotlin DSL API

2. **AI-Mocks**
   - Specialized mock server implementations built on top of Mokksy
   - Currently supports:
     - OpenAI API (`ai-mocks-openai`)
     - Anthropic API (`ai-mocks-anthropic`)
   - Allows developers to mock LLM API responses for testing and development

### Key Features

- **Streaming Support**: True support for streaming responses and Server-Side Events (SSE)
- **Response Control**: Flexibility to control server responses directly
- **Delay Simulation**: Support for simulating response delays and delays between chunks
- **Modern API**: Fluent Kotlin DSL API with Kotest Assertions
- **Error Simulation**: Ability to mock negative scenarios and error responses

## Documentation Sections

- [Getting Started](docs/getting-started/)
- [Mokksy](docs/mokksy/)
- [AI-Mocks OpenAI](docs/ai-mocks-openai/)
- [AI-Mocks Anthropic](docs/ai-mocks-anthropic/)
- [API Reference](api/)
