package dev.mokksy.aimocks.openai.official.responses

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.errors.NotFoundException
import com.openai.models.responses.EasyInputMessage
import com.openai.models.responses.ResponseCreateParams
import com.openai.models.responses.ResponseInputContent
import com.openai.models.responses.ResponseInputFile
import com.openai.models.responses.ResponseInputItem
import com.openai.models.responses.ResponseInputText
import dev.mokksy.aimocks.openai.MockOpenai
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ResponsesFileInputNegativeTest {
    private val mock = MockOpenai(verbose = true)

    private val client: OpenAIClient =
        OpenAIOkHttpClient
            .builder()
            .apiKey("dummy-key-for-tests")
            .baseUrl(mock.baseUrl())
            .build()

    private val imageResource = this.javaClass.getResource("/pipiro.jpg")!!
    private val filename = imageResource.file
    private val fileId = UUID.randomUUID().toString()
    private var temperatureValue: Double = -1.0
    private var maxCompletionTokensValue: Long = -1

    @BeforeEach
    fun setupMock() {
        temperatureValue = Random.nextDouble(0.0, 1.0)
        maxCompletionTokensValue = Random.nextLong(100, 500)
        mock.responses {
            temperature = temperatureValue
            model = "gpt-4o-mini"
            containsInputFileWithNamed(filename)
            containsInputFileWithId(fileId)
        } responds {
            assistantContent = "The file is an image."
        }
    }

    @Test
    fun `Should miss file by name`() {
        shouldThrow<NotFoundException> {
            client
                .responses()
                .create(createParams("a$fileId", filename))
                .error()
        }

        shouldThrow<NotFoundException> {
            client
                .responses()
                .create(createParams(fileId, "b$filename"))
                .error()
        }
    }

    private fun createParams(
        id: String,
        name: String,
    ): ResponseCreateParams {
        val input =
            ResponseCreateParams.Input.ofResponse(
                listOf(
                    ResponseInputItem.ofEasyInputMessage(
                        EasyInputMessage
                            .builder()
                            .role(EasyInputMessage.Role.USER)
                            .content(
                                EasyInputMessage.Content
                                    .ofResponseInputMessageContentList(
                                        listOf(
                                            ResponseInputContent.ofInputText(
                                                ResponseInputText
                                                    .builder()
                                                    .text("what's the file?")
                                                    .build(),
                                            ),
                                            ResponseInputContent.ofInputFile(
                                                ResponseInputFile
                                                    .builder()
                                                    .fileId(id)
                                                    .filename(filename)
                                                    .build(),
                                            ),
                                        ),
                                    ),
                            ).build(),
                    ),
                ),
            )
        return ResponseCreateParams
            .builder()
            .temperature(temperatureValue)
            .maxOutputTokens(maxCompletionTokensValue)
            .model(name)
            .instructions("You are a file expert")
            .input(input)
            .build()
    }
}
