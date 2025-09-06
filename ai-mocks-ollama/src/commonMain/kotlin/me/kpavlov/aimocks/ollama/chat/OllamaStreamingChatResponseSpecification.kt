package me.kpavlov.aimocks.ollama.chat

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.AbstractStreamingResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Specification for configuring streaming chat completion responses.
 *
 * This class is used to specify the content and behavior of streaming responses to chat completion requests.
 * It allows specifying the response chunks, response flow, and delay between chunks.
 */
public class OllamaStreamingChatResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<String>? = null,
    responseChunks: List<String>? = null,
    delayBetweenChunks: Duration = 0.1.seconds,
    delay: Duration = 0.seconds,
) : AbstractStreamingResponseSpecification<ChatRequest, String, String>(
        response = response,
        responseFlow = responseFlow,
        responseChunks = responseChunks,
        delayBetweenChunks = delayBetweenChunks,
        delay = delay,
    )
