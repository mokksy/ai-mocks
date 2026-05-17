---
name: integration-testing
description: |-
  Write and review parallel-safe Kotlin integration tests using shared mock servers. 
  Use when creating or modifying tests for AI-Mocks.
  Covers immutable stubs, unique match criteria, contains matchers, and parameter randomization.
compatibility: Designed for Kotlin projects using Mokksy, AI-Mocks, JUnit 5, and Kotest assertions
metadata:
  author: mokksy
  version: "1.0"
---

# Kotlin Integration Testing with Shared Mocks

## When to Apply

- Writing new integration tests with shared mock server instances
- Modifying existing tests in modules with top-level mock declarations
- Reviewing test code for parallel execution safety
- Adding new mock matchers or stub configurations

## Core Rules

### 1. Parallel-Safe by Design

Tests **must** run safely in parallel. Never disable parallel execution.

```kotlin
// ✅ CORRECT: Randomized in @BeforeEach
@BeforeEach
fun beforeEach() {
    seedValue = Random.nextInt(1, 100500)
    modelName = "llama3-$seedValue"
}

// ❌ WRONG: Hardcoded values
private val modelName = "llama3"
```

### 2. Shared Top-Level Mock Servers

All tests in a module share one top-level mock. Do not create per-test instances.

```kotlin
// ✅ CORRECT
val mockOllama = MockOllama(verbose = true)

// ❌ WRONG
private val mock = MockOllama() // Per-test instance
```

### 3. Immutable Stubs

Stubs are immutable once registered. Never modify or reconfigure another test's stub.

### 4. Unique Match Criteria

Include **all variable parameters** in both stub matchers and client requests:

```kotlin
mockOllama.chat("chat-$seedValue") {
    model = modelName
    seed = seedValue
    temperature = temperatureValue
    stream(false)
    userMessageContains("unique prompt $seedValue")
} responds { content("Response") }

// Client must use same values
client.chat("unique prompt $seedValue")
```

### 5. Contains Matcher Rule

`*Contains` matchers are case-sensitive. Client strings must contain the expected substring.

```kotlin
// ✅ CORRECT
mockOllama.chat { userMessageContains("tell me a joke") }
client.chat("Please tell me a joke about cats")

// ❌ WRONG: Case mismatch
mockOllama.chat { userMessageContains("Tell me a joke") }
client.chat("please tell me a joke")
```

### 6. Complete Parameter Coverage

Include all configurable fields (`maxTokens`, `topP`, `seed`, etc.) in both stubs and client calls.

## Test Structure Template

```kotlin
internal class ExampleTest : AbstractMockTest() {
    @Test
    fun `should handle request`() {
        mockService.endpoint("unique-$seedValue") {
            model = modelName
            seed = seedValue
            userMessageContains("expected substring")
        } responds { content("Response") }

        val result = client.call(modelName, "expected substring $seedValue")

        assertSoftly(result) {
            content shouldBe "Response"
            model shouldBe modelName
        }
    }
}

internal abstract class AbstractMockTest {
    protected var seedValue: Int = 42
    protected lateinit var modelName: String

    @BeforeEach
    fun beforeEach() {
        seedValue = Random.nextInt(1, 100500)
        modelName = "model-$seedValue"
    }

    @AfterEach
    fun afterEach() {
        mockService.verifyNoUnexpectedRequests()
    }
}
```

## Common Pitfalls

### Endpoint Mismatch

Clients may route to unexpected endpoints. Verify actual endpoints used by the client library.

```kotlin
// Lc4j may use /api/generate instead of /api/chat
mockOllama.generate { ... }  // Not mockOllama.chat { ... }
```

### Missing Seed in Options

For Ollama, seed goes in `ModelOptions`, not at root level:

```kotlin
GenerateRequest(
    model = modelName,
    options = ModelOptions(seed = seedValue)
)
```

### String Input vs Contains Matcher

```kotlin
// Client string must CONTAIN the matcher substring
mockOllama.chat { userMessageContains("hello") }
client.chat("Hello world, this is a test")  // ✅ contains "hello"
client.chat("hello")  // ❌ does not contain "hello" (exact match, not contains)
```

## Verification Checklist

- [ ] All variable parameters randomized in `@BeforeEach`
- [ ] Randomized values used in both stub matchers and client requests
- [ ] Stub names unique (include `$seedValue`)
- [ ] Contains matchers use substrings existing in client strings
- [ ] Case sensitivity respected
- [ ] All configurable fields in both stub and client
- [ ] No hardcoded values that could collide
- [ ] `verifyNoUnexpectedRequests()` in `@AfterEach`
- [ ] Kotest assertions used (`shouldBe`, `shouldNotBeNull`)
- [ ] Test names use backticks
- [ ] No KDocs on test methods

## Framework Notes

For each LLM use their native SDK as primary test client. Add smoke-level test using LangChain4j and SpringAI. 

### LangChain4j Clients

- Use `lateinit var` for client models initialized in `@BeforeEach`
- Options nested in `ModelOptions` or similar wrappers

### Spring AI Clients

- May add additional headers or fields
- Verify actual request structure with mock server logs
