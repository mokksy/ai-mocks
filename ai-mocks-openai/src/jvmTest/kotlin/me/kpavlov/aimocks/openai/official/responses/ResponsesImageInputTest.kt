package me.kpavlov.aimocks.openai.official.responses

import com.openai.models.responses.EasyInputMessage
import com.openai.models.responses.EasyInputMessage.Content.Companion.ofResponseInputMessageContentList
import com.openai.models.responses.ResponseCreateParams
import com.openai.models.responses.ResponseInputContent.Companion.ofInputImage
import com.openai.models.responses.ResponseInputContent.Companion.ofInputText
import com.openai.models.responses.ResponseInputImage
import com.openai.models.responses.ResponseInputItem.Companion.ofEasyInputMessage
import com.openai.models.responses.ResponseInputText
import io.kotest.matchers.resource.resourceAsBytes
import io.kotest.matchers.string.shouldContainIgnoringCase
import me.kpavlov.aimocks.openai.openai
import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.measureTimedValue

@Suppress("MaxLineLength")
private const val WIKIPEDIA_IMAGE_URL =
    "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg"

internal class ResponsesImageInputTest : AbstractOpenaiResponsesTest() {
    private val imageResource = this.javaClass.getResource("/pipiro.jpg")!!

    @OptIn(ExperimentalEncodingApi::class)
    private val base64Image = Base64.UrlSafe.encode(resourceAsBytes("/pipiro.jpg"))
    private val base64ImageUrl = "data:image/jpeg;base64,$base64Image"

    @Test
    @Suppress("LongMethod")
    fun `Should describe image input`() {
        modelName = "gpt-4o-mini" // cheap model
        openai.responses {
            temperature = temperatureValue
            model = modelName
            instructionsContains("You are an art expert")
            userMessageContains("what's in this image?")
            containsInputImageWithUrl(base64ImageUrl)
            containsInputImageWithUrl(WIKIPEDIA_IMAGE_URL)
            containsInputImageWithUrl(imageResource)
        } responds {
            assistantContent = """
                    The image depicts a cute, cartoonish creature resembling a small,
                    fluffy animal, possibly inspired by a bear or a similar character.
                    """
        }

        val imageInput =
            ResponseInputImage
                .builder()
                .imageUrl(base64ImageUrl)
                .detail(ResponseInputImage.Detail.AUTO)
                .build()

        val input =
            ResponseCreateParams.Input.ofResponse(
                listOf(
                    ofEasyInputMessage(
                        EasyInputMessage
                            .builder()
                            .role(EasyInputMessage.Role.USER)
                            .content(
                                ofResponseInputMessageContentList(
                                    listOf(
                                        ofInputText(
                                            ResponseInputText
                                                .builder()
                                                .text(
                                                    "what's in this image?",
                                                ).build(),
                                        ),
                                        ofInputImage(imageInput),
                                        ofInputImage(
                                            ResponseInputImage
                                                .builder()
                                                .detail(ResponseInputImage.Detail.AUTO)
                                                .imageUrl(
                                                    WIKIPEDIA_IMAGE_URL,
                                                ).build(),
                                        ),
                                    ),
                                ),
                            ).build(),
                    ),
                ),
            )
        val params =
            ResponseCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxOutputTokens(maxCompletionTokensValue)
                .model(modelName)
                .instructions("You are an art expert")
                .input(input)
                .build()

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

        assistantText shouldContainIgnoringCase "creature"

        verifyResponse(response)
    }
}
