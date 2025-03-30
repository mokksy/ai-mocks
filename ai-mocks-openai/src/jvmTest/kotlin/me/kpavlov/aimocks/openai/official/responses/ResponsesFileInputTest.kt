package me.kpavlov.aimocks.openai.official.responses

import com.openai.errors.NotFoundException
import com.openai.models.responses.EasyInputMessage
import com.openai.models.responses.ResponseCreateParams
import com.openai.models.responses.ResponseInputContent
import com.openai.models.responses.ResponseInputFile
import com.openai.models.responses.ResponseInputItem
import com.openai.models.responses.ResponseInputText
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldStartWith
import me.kpavlov.aimocks.openai.official.AbstractOpenaiTest
import me.kpavlov.aimocks.openai.openai
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URL
import java.util.UUID
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.measureTimedValue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ResponsesFileInputTest : AbstractOpenaiTest() {
    private lateinit var imageResource: URL
    private lateinit var filename: String
    private lateinit var fileId: String

    @OptIn(ExperimentalEncodingApi::class)
    @BeforeAll
    fun beforeAll() {
        imageResource = this.javaClass.getResource("/pipiro.jpg")!!
        filename = imageResource.file
        fileId = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setupMock() {
        modelName = "gpt-4o-mini" // cheap model
        openai.responses {
            temperature = temperatureValue
            model = modelName

            containsInputFileWithNamed(filename)
            containsInputFileWithId(fileId)
        } responds {
            assistantContent = "The file is an image."
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Should match file`() {
        val params =
            createParams(fileId, modelName)

        val timedValue =
            measureTimedValue {
                client.responses().create(params)
            }

        val response = timedValue.value

        logger.debug { "Response: $response" }
        val message = response.output().first().asMessage()

        val assistantText =
            message
                .content()
                .first()
                .asOutputText()
                .text()
        logger.info { "Assistant text: $assistantText" }

        assistantText shouldContainIgnoringCase "image"

        response.model().asString() shouldStartWith modelName
    }

    @Test
    @Suppress("LongMethod")
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
                    ResponseInputItem.Companion.ofEasyInputMessage(
                        EasyInputMessage.Companion
                            .builder()
                            .role(EasyInputMessage.Role.USER)
                            .content(
                                EasyInputMessage.Content.Companion
                                    .ofResponseInputMessageContentList(
                                        listOf(
                                            ResponseInputContent.Companion.ofInputText(
                                                ResponseInputText.Companion
                                                    .builder()
                                                    .text(
                                                        "what's the file?",
                                                    ).build(),
                                            ),
                                            ResponseInputContent.Companion.ofInputFile(
                                                ResponseInputFile.Companion
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
        val params =
            ResponseCreateParams.Companion
                .builder()
                .temperature(temperatureValue)
                .maxOutputTokens(maxCompletionTokensValue)
                .model(name)
                .instructions("You are a file expert")
                .input(input)
                .build()
        return params
    }
}
