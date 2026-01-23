package dev.mokksy.aimocks.anthropic.official

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import dev.mokksy.aimocks.anthropic.AbstractAnthropicIntegrationTest
import dev.mokksy.aimocks.anthropic.anthropic

@Suppress("AbstractClassCanBeConcreteClass")
internal abstract class AbstractAnthropicTest : AbstractAnthropicIntegrationTest() {
    protected val client: AnthropicClient =
        AnthropicOkHttpClient
            .builder()
            .apiKey("my-anthropic-api-key")
            .baseUrl(anthropic.baseUrl())
            .responseValidation(true)
            .build()
}
