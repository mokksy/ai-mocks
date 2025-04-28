# Mokksy and AI-Mocks Project Guidelines

## Project Overview

Mokksy and AI-Mocks are a suite of tools designed for mocking HTTP and LLM (Large Language Model) APIs for testing and development purposes.

### Components

1. **Mokksy**
   - A mock HTTP server built with Kotlin and Ktor
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

### Project Structure

- `ai-mocks-a2a`: Agent2Agent (A2A) protocol mocks
- `ai-mocks-a2a-models`: Agent2Agent (A2A) protocol models
- `ai-mocks-core`: Core functionality shared across AI-Mocks implementations
- `ai-mocks-openai`: OpenAI API mock implementation
- `ai-mocks-anthropic`: Anthropic API mock implementation
- `mokksy`: The underlying mock HTTP server

### Development Guidelines

1. **Code Style: Kotlin**
   - Follow Kotlin coding conventions
   - Use the provided `.editorconfig` for consistent formatting
   - Use Kotlin typesafe DSL builders where possible and prioritize fluent builders style over standard builder methods. If DSL builders produce less readable code, use standard setter methods.
   - Use Kotlin's `val` for immutable properties and `var` for mutable properties

2. **Code Style: Java**
  - Use the provided `.editorconfig` for consistent formatting
  - For Java code prefer fluent DSL style over standard bean getters and setter methods

1. **Testing**
   - Write comprehensive tests for new features
   - Ensure backward compatibility when making changes
   - Write tests on Kotlin with kotlin-test and Kotest with infix form assertions `shouldBe` instead of Assertj's `assertThat(...)`.
   - Prioritize test readability
   - When asked to write tests in Java: use JUnit5, Mockito, AssertJ core

2. **Documentation**
   - Update README files when adding new features
   - Document API changes in the appropriate module's documentation
   - Write tutorials in Hugo markdown /docs/content/docs. 

3. **Contributions**
   - Follow the guidelines in CONTRIBUTING.md
   - Create pull requests for new features or bug fixes
   - Ensure all tests pass before submitting
   - Never commit and push automatically

### Getting Started

For detailed usage instructions and examples, refer to:
- Main README.md for project overview
- Module-specific README files for detailed API documentation
- Sample code in each module's samples directory

### Building the Project

```shell
gradle build
```

or using Make:

```shell
make
```
