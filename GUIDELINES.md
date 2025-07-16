# Mokksy and AI-Mocks Project Guidelines

## Project Overview

Mokksy and AI-Mocks are a suite of tools designed for mocking HTTP and LLM (Large Language Model) APIs for testing and
development purposes.

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
    - Google Gemini (`ai-mocks-gemini`)
  - Allows developers to mock LLM API responses for testing and development

### Key Features

- **Streaming Support**: True support for streaming responses and Server-Side Events (SSE)
- **Response Control**: Flexibility to control server responses directly
- **Delay Simulation**: Support for simulating response delays and delays between chunks
- **Modern API**: Fluent Kotlin DSL API with Kotest Assertions
- **Error Simulation**: Ability to mock negative scenarios and error responses

### Project Structure

- `ai-mocks-a2a-client`: Agent2Agent (A2A) client. Used for testing
- `ai-mocks-a2a`: Agent2Agent (A2A) protocol mocks
- `ai-mocks-a2a-models`: Agent2Agent (A2A) protocol models
- `ai-mocks-core`: Core functionality shared across AI-Mocks implementations
- `ai-mocks-openai`: OpenAI API mock implementation
- `ai-mocks-anthropic`: Anthropic API mock implementation
- `ai-mocks-gemini`: Google Gemini/GenAI API mock implementation
- `mokksy`: The underlying mock HTTP server

## Development Guidelines

### 1. Code Style

#### Kotlin

- Follow Kotlin coding conventions
- Use the provided `.editorconfig` for consistent formatting
- Use Kotlin typesafe DSL builders where possible and prioritize fluent builders style over standard builder methods.
    If DSL builders produce less readable code, use standard setter methods.
- Use Kotlin's `val` for immutable properties and `var` for mutable properties
- Ensure to preserve backward compatibility when making changes

#### Java

- Use the provided `.editorconfig` for consistent formatting
- For Java code prefer fluent DSL style over standard bean getters and setter methods

3. **Testing**
  - Write comprehensive tests for new features
- Write Kotlin tests with kotlin-test and Kotest-assertions with infix form assertions `shouldBe` instead of
  Assertj's `assertThat(...)`.
- Use Kotest's `withClue("<failure reason>")` to describe failure reasons, but only when the assertion is NOT obvious.
  Remove obvious cases for simplicity.
- Use `assertSoftly(subject) { ... }` to perform multiple assertions. Never use `assertSoftly { }` to verify properties
  of
  different subjects, or when there is only one assertion per subject. Avoid using `assertSoftly(this) { ... }`
  - Prioritize test readability
  - When asked to write tests in Java: use JUnit5, Mockito, AssertJ core

4. **Documentation**
  - Update README files when adding new features
  - Document API changes in the appropriate module's documentation
- Write tutorials in Hugo markdown /docs/content/docs

### Project Documentation

The project uses two main tools for documentation:

1. **Dokka** - For API documentation generation from code
2. **Hugo** - For building the documentation website

#### Local Documentation Generation

To generate documentation locally:

1. Generate API documentation with Dokka:
   ```shell
   ./gradlew :docs:dokkaGenerate
   ```
   This will generate API documentation in `docs/public/apidocs/`.

2. Build the Hugo site:
   ```shell
   cd docs
   hugo
   ```
   This will generate the complete site in `docs/public/`.

3. Preview the documentation site locally:
   ```shell
   cd docs
   hugo server
   ```
   This will start a local server (typically at http://localhost:1313/) where you can preview the documentation.

#### Documentation Structure

- API reference documentation is generated from code using Dokka
- User guides and tutorials are written in Markdown in the `docs/content/docs` directory
- Each AI-Mocks module should have its own documentation page

#### Publishing Documentation

Documentation is automatically published to [mokksy.dev](https://mokksy.dev/) when:

- A new release is created
- Changes are pushed to the main branch
- The documentation workflow is manually triggered

The publishing process is handled by the GitHub Actions workflow in `.github/workflows/docs.yaml`.

#### Documentation Content Guidelines

When creating or updating documentation, focus on the following aspects:

1. **API Documentation**:
  - Ensure all public APIs have proper KDoc/JavaDoc comments
  - Include examples of how to use the API
  - Document parameters, return values, and exceptions
  - Explain the purpose and behavior of each class and method

2. **User Guides and Tutorials**:
  - Start with a clear introduction explaining what the module does
  - Include step-by-step instructions with code examples
  - Provide complete working examples that users can copy and adapt.
  - Explain common use cases and best practices
  - Include troubleshooting information for common issues
  - **Double-check that documentation matches the existing code and tests. Never make up anything not implemented as
    code!**

3. **Documentation for AI-Mocks Modules**:
   Each AI-Mocks module should have documentation that includes:
  - Installation instructions
  - Basic usage examples
  - Configuration options (preferably in table format)
  - Advanced usage examples
  - Integration with relevant SDKs and frameworks
  - Examples of mocking both successful and error responses
  - Examples of streaming responses if applicable

4. **Contributions**
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
