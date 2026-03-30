package dev.mokksy.aimocks.openai.official.embeddings

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.errors.NotFoundException
import com.openai.models.embeddings.EmbeddingCreateParams
import dev.mokksy.aimocks.openai.MockOpenai
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class EmbeddingsNegativeOpenaiTest {
    private val mock = MockOpenai(verbose = true)

    private val client: OpenAIClient =
        OpenAIOkHttpClient
            .builder()
            .apiKey("dummy-key-for-tests")
            .baseUrl(mock.baseUrl())
            .build()

    @Test
    fun `Should respond 404 to embeddings request when input not matched`() {
        val seedValue = Random.nextInt(1, 100500)
        val input = "Hello world $seedValue"
        mock.embeddings {
            model = "text-embedding-3-small"
            inputContains("Hello2")
        } responds {
            delay = 50.milliseconds
            embeddings(
                listOf(0.1f, 0.2f, 0.3f),
            )
        }
        val params =
            EmbeddingCreateParams
                .builder()
                .model("text-embedding-3-small")
                .input(EmbeddingCreateParams.Input.ofString(input))
                .build()

        val exception =
            assertThrows<NotFoundException> {
                client
                    .embeddings()
                    .create(params)
            }
        exception.message shouldContain "404:"
    }
}
