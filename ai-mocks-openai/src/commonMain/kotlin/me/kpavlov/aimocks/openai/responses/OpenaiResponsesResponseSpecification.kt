package me.kpavlov.aimocks.openai.responses

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ChatResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Suppress("LongParameterList")
public class OpenaiResponsesResponseSpecification(
    response: AbstractResponseDefinition<Response>,
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    public var delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
) : ChatResponseSpecification<CreateResponseRequest, Response>(response = response) {
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

    public fun delayMillis(value: Long): OpenaiResponsesResponseSpecification =
        apply {
            this.delay = value.milliseconds
        }
}
