package me.kpavlov.aimocks.openai.responses

import io.kotest.matchers.string.contain
import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.mokksy.utils.asBase64DataUrl
import java.net.URL

public open class OpenaiResponsesRequestSpecification(
    public var seed: Int? = null,
) : ModelRequestSpecification<CreateResponseRequest>() {
    public fun seed(value: Int): OpenaiResponsesRequestSpecification =
        apply {
            this.seed =
                value
        }

    override fun systemMessageContains(substring: String) {
        instructionsContains(substring)
    }

    public fun instructionsContains(substring: String) {
        requestBody.add(OpenaiResponsesMatchers.instructionsContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBodyString += contain(substring)
        // requestBody.add(OpenAiMatchers.userMessageContains(substring))
    }

    /**
     * Checks if the input includes an image with the specified URL.
     *
     * @param imageUrl The URL of the image to check for in the input. Might be Base64 image url
     */
    public fun containsInputImageWithUrl(imageUrl: String) {
        requestBodyString += contain(imageUrl)
//        requestBody.add(OpenAiResponsesMatchers.containsInputImageWithUrl(substring))
    }

    private fun containsInputImageWithUrl(url: URL) {
        val dataUrl = url.asBase64DataUrl()
        TODO("Not yet implemented")
    }
}
