package me.kpavlov.aimocks.openai.official

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import me.kpavlov.aimocks.openai.AbstractMockOpenaiTest
import me.kpavlov.aimocks.openai.openai

internal abstract class AbstractOpenaiTest : AbstractMockOpenaiTest() {
    protected val client: OpenAIClient =
        OpenAIOkHttpClient
            .builder()
            .apiKey("my-key")
            .baseUrl(openai.baseUrl())
            .responseValidation(true)
            .build()
}
