# AI-Mocks

[![Maven Central](https://img.shields.io/maven-central/v/dev.mokksy.aimocks/ai-mocks-core)](https://repo1.maven.org/maven2/dev/mokksy/aimocks/ai-mocks-core/)
[![Kotlin CI](https://github.com/mokksy/ai-mocks/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/mokksy/ai-mocks/actions/workflows/gradle.yml)
![GitHub branch status](https://img.shields.io/github/checks-status/mokksy/ai-mocks/main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/100bb4b0f6744188b86f38464a48da93)](https://app.codacy.com/gh/mokksy/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Coverage](https://app.codacy.com/project/badge/Coverage/100bb4b0f6744188b86f38464a48da93)](https://app.codacy.com/gh/mokksy/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![codecov](https://codecov.io/github/mokksy/ai-mocks/graph/badge.svg?token=449G80QY5S)](https://codecov.io/github/mokksy/ai-mocks)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/mokksy/ai-mocks?utm_source=oss&utm_medium=github&utm_campaign=mokksy%2Fai-mocks&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

[![Documentation](https://img.shields.io/badge/docs-website-blue)](https://mokksy.dev/)
[![API Reference](https://img.shields.io/badge/api-reference-blue)](https://mokksy.dev/apidocs/)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/mokksy/ai-mocks)

![Kotlin API](https://img.shields.io/badge/Kotlin-2.2-%237F52FF.svg?logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/JVM-17-%23ED8B00.svg)

_AI-Mocks_ are mock LLM (Large Language Model) servers built on [Mokksy](https://github.com/mokksy/mokksy/), a mock server inspired by WireMock, with support for
response streaming and Server-Side Events (SSE). They are designed to build, test, and mock LLM responses for development purposes.


[![Buy me a Coffee](https://cdn.buymeacoffee.com/buttons/default-orange.png)](https://buymeacoffee.com/mailsk)

## Mokksy

**[Mokksy](https://github.com/mokksy/mokksy)** is a mock HTTP server built with [Kotlin](https://kotlinlang.org/)
and [Ktor](https://ktor.io/). It addresses the limitations of WireMock by supporting true SSE and streaming responses,
making it extreamly useful for integration testing LLM clients.

# AI-Mocks

**AI-Mocks** is a set of specialized mock server implementations (e.g., mocking OpenAI API) built using Mokksy.

It supports mocking following AI services:
1. [OpenAI](https://platform.openai.com/docs/api-reference/) - [ai-mocks-openai](https://mokksy.dev/docs/ai-mocks/openai/)
2. [Anthropic](https://docs.anthropic.com/en/api) - [ai-mocks-anthropic](https://mokksy.dev/docs/ai-mocks/anthropic/)
3. [Google VertexAI Gemini](https://cloud.google.com/vertex-ai/generative-ai/docs/model-reference/inference) - [ai-mocks-gemini](https://mokksy.dev/docs/ai-mocks/gemini/)
4. [Ollama](https://github.com/ollama/ollama/blob/main/docs/api.md) - [ai-mocks-ollama](https://mokksy.dev/docs/ai-mocks/ollama/)
5. [Agent-to-Agent (A2A) Protocol](https://a2a-protocol.org/latest/specification/) - [ai-mocks-a2a](https://mokksy.dev/docs/ai-mocks/a2a/)

## Feature Support Matrix

| Feature              | OpenAI    | Anthropic | Gemini | Ollama   | A2A                                  |
|----------------------|-----------|-----------|--------|----------|--------------------------------------|
| **Chat Completions** | ✅         | ✅         | ✅      | ✅        | ✅                                    |
| **Streaming**        | ✅         | ✅         | ✅      | ✅        | ✅                                    |
| **Embeddings**       | ✅         | ❌         | ❌      | ✅        | ❌                                    |
| **Moderation**       | ✅         | ❌         | ❌      | ❌        | ❌                                    |
| **Additional APIs**  | Responses | -         | -      | Generate | Full A2A Protocol<br/>(11 endpoints) |


## How to build

Building project locally:

```shell
./gradlew build
```

or using Make:

```shell
make
```

## Contributing

I do welcome contributions! Please see the [Contributing Guidelines](CONTRIBUTING.md) for details.

