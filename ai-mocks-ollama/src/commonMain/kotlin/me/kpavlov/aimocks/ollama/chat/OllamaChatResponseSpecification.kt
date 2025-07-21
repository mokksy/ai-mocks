package me.kpavlov.aimocks.ollama.chat

import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Specification for configuring chat completion responses.
 *
 * This class is used to specify the content and behavior of responses to chat completion requests.
 * It allows specifying the assistant message content, thinking content, and tool calls.
 *
 * @property assistantContent The content of the assistant's response
 * @property thinking The thinking process of the model (for thinking models)
 * @property toolCalls The tool calls to include in the response
 */
public class OllamaChatResponseSpecification(
    response: AbstractResponseDefinition<ChatResponse>,
    public var assistantContent: String = "This is a mock response from Ollama.",
    public var thinking: String? = null,
    public var toolCalls: List<Map<String, Any>>? = null,
    public var finishReason: String? = "stop",
    delay: Duration = 0.seconds,
) : AbstractResponseSpecification<ChatRequest, ChatResponse>(
    response = response,
    delay = delay,
) {
    /**
     * Specifies the content of the assistant's response.
     *
     * @param content The assistant's response content
     * @return This specification for method chaining
     */
    public fun content(content: String): OllamaChatResponseSpecification {
        this.assistantContent = content
        return this
    }

    /**
     * Specifies the thinking process of the model.
     *
     * @param thinking The thinking process
     * @return This specification for method chaining
     */
    public fun thinking(thinking: String): OllamaChatResponseSpecification {
        this.thinking = thinking
        return this
    }

    /**
     * Specifies the tool calls to include in the response.
     *
     * @param toolCalls The tool calls
     * @return This specification for method chaining
     */
    public fun toolCalls(toolCalls: List<Map<String, Any>>): OllamaChatResponseSpecification {
        this.toolCalls = toolCalls
        return this
    }

    /**
     * Creates a message object for the response.
     *
     * @return The message object
     */
    internal fun createMessage(): Message {
        val message = Message(
            role = "assistant",
            content = assistantContent,
            thinking = thinking
        )

        return message
    }
}
