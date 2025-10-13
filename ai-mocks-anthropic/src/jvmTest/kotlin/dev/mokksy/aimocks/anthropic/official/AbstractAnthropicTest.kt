package dev.mokksy.aimocks.anthropic.official

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import dev.mokksy.aimocks.anthropic.AbstractAnthropicIntegraitonTest
import dev.mokksy.aimocks.anthropic.anthropic

internal abstract class AbstractAnthropicTest : AbstractAnthropicIntegraitonTest() {
    protected val client: AnthropicClient =
        AnthropicOkHttpClient
            .builder()
            .apiKey("my-anthropic-api-key")
            .baseUrl(anthropic.baseUrl())
            .responseValidation(true)
            .build()
}
