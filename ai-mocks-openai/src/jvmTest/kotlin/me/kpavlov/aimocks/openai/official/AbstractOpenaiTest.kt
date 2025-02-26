package me.kpavlov.aimocks.openai

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient

internal abstract class AbstractOpenaiTest : AbstractMockOpenaiTest() {
    protected val client: OpenAIClient =
        OpenAIOkHttpClient
            .builder()
            .apiKey("my-key")
            .baseUrl("http://127.0.0.1:${openai.port()}/v1")
            .responseValidation(true)
            .build()
}
