package dev.mokksy.aimocks.openai.official.embeddings

import com.openai.errors.BadRequestException
import com.openai.errors.NotFoundException
import com.openai.models.embeddings.EmbeddingCreateParams
import dev.mokksy.aimocks.openai.official.AbstractOpenaiTest
import dev.mokksy.aimocks.openai.openai
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

internal class EmbeddingsOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to embeddings request with single string input`() {
        val input = "Hello world $seedValue"
        openai.embeddings {
            model = "text-embedding-3-small"
            inputContains("Hello")
            inputContains(input)
            stringInput(input)
        } responds {
            delay = 200.milliseconds
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

        val timedValue =
            measureTimedValue {
                client
                    .embeddings()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThanOrEqualTo 200.milliseconds
        val result = timedValue.value

        result.model() shouldBe "text-embedding-3-small"
        result.data() shouldHaveSize 1
        val embedding = result.data().first()
        embedding.embedding() shouldBe listOf(0.1f, 0.2f, 0.3f)
        embedding.index() shouldBe 0
    }

    @Test
    fun `Should respond 404 to embeddings request when input not matched`() {
        val input = "Hello world $seedValue"
        openai.embeddings {
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

    @Test
    fun `Should respond to embeddings request with list of strings`() {
        val inputs = listOf("Hello", "world", "$seedValue")
        openai.embeddings {
            model = "text-embedding-3-small"
            stringListInput(inputs)
        } responds {
            delay = 100.milliseconds
            embeddings(
                listOf(0.1f, 0.2f, 0.3f),
                listOf(0.4f, 0.5f, 0.6f),
            )
        }

        val params =
            EmbeddingCreateParams
                .builder()
                .model("text-embedding-3-small")
                .input(EmbeddingCreateParams.Input.ofArrayOfStrings(inputs))
                .build()

        val timedValue =
            measureTimedValue {
                client
                    .embeddings()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThanOrEqualTo 100.milliseconds
        val result = timedValue.value

        result.model() shouldBe "text-embedding-3-small"
        result.data() shouldHaveSize 2
        result.data()[0].embedding() shouldBe listOf(0.1f, 0.2f, 0.3f)
        result.data()[0].index() shouldBe 0
        result.data()[1].embedding() shouldBe listOf(0.4f, 0.5f, 0.6f)
        result.data()[1].index() shouldBe 1
    }

    @Test
    fun `Should respond with unexpected error for embeddings`() {
        openai
            .embeddings {
                model = modelName
                stringInput("boom")
            }.respondsError(String::class) {
                body = "Kaboom!"
                contentType = ContentType.Text.Plain
                httpStatus = HttpStatusCode.BadRequest
                delay = 200.milliseconds
            }

        val params =
            EmbeddingCreateParams
                .builder()
                .model(modelName)
                .input(EmbeddingCreateParams.Input.ofString("boom"))
                .build()

        val timedValue =
            measureTimedValue {
                shouldThrow<BadRequestException> {
                    client
                        .embeddings()
                        .create(params)
                }
            }

        timedValue.duration shouldBeGreaterThan 200.milliseconds
        val exception = timedValue.value
        exception.statusCode() shouldBe HttpStatusCode.BadRequest.value
    }
}
