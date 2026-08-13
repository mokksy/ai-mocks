# AI-Mocks

[![Maven Central](https://img.shields.io/maven-central/v/dev.mokksy.aimocks/ai-mocks-core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/dev.mokksy.aimocks/ai-mocks-core)
[![Kotlin CI](https://github.com/mokksy/ai-mocks/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/mokksy/ai-mocks/actions/workflows/gradle.yml)
![GitHub branch status](https://img.shields.io/github/checks-status/mokksy/ai-mocks/main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/100bb4b0f6744188b86f38464a48da93)](https://app.codacy.com/gh/mokksy/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Coverage](https://app.codacy.com/project/badge/Coverage/100bb4b0f6744188b86f38464a48da93)](https://app.codacy.com/gh/mokksy/ai-mocks/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![codecov](https://codecov.io/github/mokksy/ai-mocks/graph/badge.svg?token=449G80QY5S)](https://codecov.io/github/mokksy/ai-mocks)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/mokksy/ai-mocks?utm_source=oss&utm_medium=github&utm_campaign=mokksy%2Fai-mocks&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

[![Documentation](https://img.shields.io/badge/docs-website-blue)](https://mokksy.dev/docs/ai-mocks/)
[![API Reference](https://img.shields.io/badge/api-reference-blue)](https://mokksy.github.io/ai-mocks/)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/mokksy/ai-mocks)

![Kotlin API](https://img.shields.io/badge/Kotlin-2.2-%237F52FF.svg?logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/JVM-17-%23ED8B00.svg)

**AI-Mocks** provides provider-compatible mock servers for AI integration testing in Kotlin and Java. Test clients for OpenAI, Anthropic, Gemini, Ollama, and the Agent-to-Agent (A2A) protocol through real HTTP and streaming behavior, without sending requests to live provider APIs.

AI-Mocks is built on [Mokksy](https://github.com/mokksy/mokksy), the HTTP and SSE mock server for deterministic integration testing.
Use [Mokksy](https://mokksy.dev/docs/mokksy/) for general HTTP APIs, streaming APIs, and failure simulation.
Use AI-Mocks when the service under test exposes an AI provider-compatible or A2A API.

- Documentation: [mokksy.dev/docs/ai-mocks](https://mokksy.dev/docs/ai-mocks/)
- Integration guides: [mokksy.dev/docs/integrations](https://mokksy.dev/docs/integrations/)
- API reference: [mokksy.github.io/ai-mocks](https://mokksy.github.io/ai-mocks/)
- Core HTTP/SSE mock server: [github.com/mokksy/mokksy](https://github.com/mokksy/mokksy)

[![Buy me a Coffee](https://cdn.buymeacoffee.com/buttons/default-orange.png)](https://buymeacoffee.com/mailsk)

## Choose the right layer

| What you need to test | Start with |
|-----------------------|------------|
| General HTTP endpoints, Server-Sent Events (SSE), streamed responses, delays, or status/error scenarios | [Mokksy](https://mokksy.dev/docs/mokksy/) |
| Provider- or protocol-shaped AI requests and responses using SDKs or frameworks | [AI-Mocks](https://mokksy.dev/docs/ai-mocks/) |
| An application that combines business HTTP integrations with AI providers | Mokksy for general dependencies and AI-Mocks for provider APIs |

## Supported providers and protocols

| Provider or protocol | Module | Supported surface | Documentation |
|----------------------|--------|-------------------|---------------|
| OpenAI | `ai-mocks-openai` | Chat Completions, Responses, streaming, embeddings, moderation | [OpenAI](https://mokksy.dev/docs/ai-mocks/openai/) |
| Anthropic | `ai-mocks-anthropic` | Messages and streaming | [Anthropic](https://mokksy.dev/docs/ai-mocks/anthropic/) |
| Google Gemini | `ai-mocks-gemini` | Generate Content and streaming | [Gemini](https://mokksy.dev/docs/ai-mocks/gemini/) |
| Ollama | `ai-mocks-ollama` | Chat, generate, streaming, embeddings | [Ollama](https://mokksy.dev/docs/ai-mocks/ollama/) |
| Agent-to-Agent (A2A) | `ai-mocks-a2a` | A2A protocol behavior and streaming | [A2A](https://mokksy.dev/docs/ai-mocks/a2a/) |

## Tested integrations

The repository integration tests exercise these client and framework combinations:

| AI-Mocks module | Tested clients and frameworks |
|-----------------|-------------------------------|
| OpenAI | Official OpenAI Java SDK, LangChain4j, Spring AI |
| Anthropic | Official Anthropic Java SDK, LangChain4j |
| Gemini | Google Gen AI Java SDK, LangChain4j, Spring AI |
| Ollama | LangChain4j, Spring AI |

## Quick start with OpenAI

Add the OpenAI test dependency:

```kotlin
dependencies {
    testImplementation("dev.mokksy.aimocks:ai-mocks-openai-jvm:$latestVersion")
}
```

Declare a deterministic completion response and point the client under test to `openai.baseUrl()`:

```kotlin
import dev.mokksy.aimocks.openai.MockOpenai

val openai = MockOpenai(verbose = true)

openai.completion {
    model = "gpt-4o-mini"
    userMessageContains("Hello")
} responds {
    assistantContent = "Hello from the mock"
    finishReason = "stop"
}

// Configure the OpenAI SDK or framework under test to use openai.baseUrl().
```

The DSL and `baseUrl()` configuration pattern are exercised by the official OpenAI SDK integration tests in this repository. For complete SDK and framework examples, see the [AI-Mocks documentation](https://mokksy.dev/docs/ai-mocks/) and [integration guides](https://mokksy.dev/docs/integrations/).

## Why AI-Mocks?

- Exercise provider-compatible requests and responses with the real HTTP client configuration used by your application.
- Test streaming responses deterministically in CI without live provider credentials, network dependence, quotas, or provider rate limits.
- Verify application behavior for provider responses and streamed events before relying on live external services.
- Keep provider-specific behavior in AI-Mocks while using [Mokksy](https://mokksy.dev/docs/mokksy/) for other HTTP dependencies and failure scenarios.

## Build locally

```shell
./gradlew build
```

or:

```shell
make
```

## Contributing

Contributions are welcome. See the [Contributing Guidelines](CONTRIBUTING.md) for details.
