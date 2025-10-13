package dev.mokksy.aimocks.ollama.chat

import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
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
     * Sets the assistant's response content.
     *
     * @param content The content to use for the assistant's reply.
     * @return This specification instance for method chaining.
     */
    public fun content(content: String): OllamaChatResponseSpecification {
        this.assistantContent = content
        return this
    }

    /**
     * Sets the thinking process content for the assistant's response.
     *
     * @param thinking The content representing the assistant's thought process.
     * @return This specification instance for method chaining.
     */
    public fun thinking(thinking: String): OllamaChatResponseSpecification {
        this.thinking = thinking
        return this
    }

    /**
     * Sets the tool calls to be included in the chat response.
     *
     * @param toolCalls A list of tool call definitions to attach to the response.
     * @return This specification instance for method chaining.
     */
    public fun toolCalls(toolCalls: List<Map<String, Any>>): OllamaChatResponseSpecification {
        this.toolCalls = toolCalls
        return this
    }

    /**
     * Constructs a `Message` representing the assistant's response, including content and optional thinking information.
     *
     * @return A `Message` object with the role set to "assistant".
     */
    internal fun createMessage(): Message {
        val message =
            Message(
                role = "assistant",
                content = assistantContent,
                thinking = thinking,
            )

        return message
    }
}
