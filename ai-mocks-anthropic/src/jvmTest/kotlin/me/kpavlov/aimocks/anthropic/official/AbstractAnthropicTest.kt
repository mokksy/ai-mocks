package me.kpavlov.aimocks.anthropic.official

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import me.kpavlov.aimocks.anthropic.AbstractAnthropicIntegraitonTest
import me.kpavlov.aimocks.anthropic.anthropic

internal abstract class AbstractAnthropicTest : AbstractAnthropicIntegraitonTest() {
    protected val client: AnthropicClient =
        AnthropicOkHttpClient
            .builder()
            .apiKey("my-anthropic-api-key")
            .baseUrl(anthropic.baseUrl())
            .responseValidation(true)
            .build()
}
