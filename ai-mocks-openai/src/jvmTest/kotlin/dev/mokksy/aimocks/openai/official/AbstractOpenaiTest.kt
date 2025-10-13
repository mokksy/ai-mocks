package dev.mokksy.aimocks.openai.official

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import dev.mokksy.aimocks.openai.AbstractMockOpenaiTest
import dev.mokksy.aimocks.openai.openai
import me.kpavlov.finchly.TestEnvironment

internal abstract class AbstractOpenaiTest : AbstractMockOpenaiTest() {
    protected val client: OpenAIClient =
        OpenAIOkHttpClient
            .builder()
            .apiKey(TestEnvironment.get("OPENAI_API_KEY", "dummy-key-for-tests")!!)
            .baseUrl(openai.baseUrl())
            .responseValidation(true)
            .build()
}
