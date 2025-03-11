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

- `ai-mocks-core`: Core functionality shared across AI-Mocks implementations
- `ai-mocks-openai`: OpenAI API mock implementation
- `ai-mocks-anthropic`: Anthropic API mock implementation
- `mokksy`: The underlying mock HTTP server

### Development Guidelines

1. **Code Style**
   - Follow Kotlin coding conventions
   - Use the provided `.editorconfig` for consistent formatting

2. **Testing**
   - Write comprehensive tests for new features
   - Ensure backward compatibility when making changes

3. **Documentation**
   - Update README files when adding new features
   - Document API changes in the appropriate module's documentation

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
