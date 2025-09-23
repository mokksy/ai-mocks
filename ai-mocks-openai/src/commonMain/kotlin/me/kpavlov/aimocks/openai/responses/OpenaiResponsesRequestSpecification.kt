package me.kpavlov.aimocks.openai.responses

import me.kpavlov.aimocks.core.AbstractInferenceRequestSpecification
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.mokksy.utils.asBase64DataUrl
import java.net.URL

/**
 * Defines specifications for building and validating OpenAI response requests. This class extends
 * the functionalities provided by [AbstractInferenceRequestSpecification] to incorporate additional checks
 * specific to handling OpenAI response-related requests.
 *
 * @constructor Initializes the specification with optional parameters.
 * @param seed An optional random seed value for reproducible results.
 * @author Konstantin Pavlov
 */
public open class OpenaiResponsesRequestSpecification(
    public var seed: Int? = null,
) : AbstractInferenceRequestSpecification<CreateResponseRequest>() {
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
        requestBody.add(OpenaiResponsesMatchers.userMessageContains(substring))
    }

    /**
     * Checks if the input contains a file with the specified filename.
     *
     * @param filename The name of the file to check for in the input.
     */
    public fun containsInputFileWithNamed(filename: String) {
        requestBody.add(OpenaiResponsesMatchers.containsInputFileNamed(filename))
    }

    public fun containsInputFileWithId(fileId: String) {
        requestBody.add(OpenaiResponsesMatchers.containsInputFileWithId(fileId))
    }

    /**
     * Checks if the input includes an image with the specified URL.
     *
     * @param imageUrl The URL of the image to check for in the input. Might be Base64 image url
     */
    public fun containsInputImageWithUrl(imageUrl: String) {
        requestBody.add(OpenaiResponsesMatchers.containsInputImageWithUrl(imageUrl))
    }

    /**
     * Checks if the input includes an image with the specified URL as Base64 data URL.
     *
     * @param url The URL of the image to check for in the input.
     * The content of the URL will be converted to a Base64 data URL string.
     */
    public fun containsInputImageWithUrl(url: URL) {
        val dataUrl = url.asBase64DataUrl()
        containsInputImageWithUrl(dataUrl)
    }
}
