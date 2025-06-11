package me.kpavlov.aimocks.openai.responses

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.model.responses.Response
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Represents the specification for an OpenAI API response configuration.
 * This class enables the customization of response properties, such as
 * content, delays, and finishing behavior. It extends `ChatResponseSpecification`
 * with additional properties and methods specific to the OpenAI API.
 *
 * @constructor
 * @param response The base definition of the response configuration.
 * @param assistantContent The textual content of the assistant's response. Defaults to an empty string.
 * @param responseFlow A flow of strings representing incremental response content. Defaults to `null`.
 * @param responseChunks A list of response content chunks for streaming scenarios. Defaults to `null`.
 * @param delayBetweenChunks The delay applied between sending chunks of the response. Defaults to `Duration.ZERO`.
 * @param delay A general delay for the response. Defaults to `Duration.ZERO`.
 * @param finishReason The reason for the response termination. Defaults to "stop".
 * @author Konstantin Pavlov
 */
@Suppress("LongParameterList")
public class OpenaiResponsesResponseSpecification(
    response: AbstractResponseDefinition<Response>,
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
) : ResponseSpecification<CreateResponseRequest, Response>(
    response = response,
    delay = delay
) {
    public fun assistantContent(content: String): OpenaiResponsesResponseSpecification =
        apply {
            this.assistantContent =
                content
        }

    public fun finishReason(finishReason: String): OpenaiResponsesResponseSpecification =
        apply {
            this.finishReason =
                finishReason
        }

}
