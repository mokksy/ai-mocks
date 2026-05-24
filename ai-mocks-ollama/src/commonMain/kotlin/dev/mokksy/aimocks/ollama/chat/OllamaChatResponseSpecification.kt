package dev.mokksy.aimocks.ollama.chat

import dev.mokksy.aimocks.core.AbstractResponseSpecification
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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
    public var assistantContent: String = "This is a mock response from Ollama.",
    public var thinking: String? = null,
    public var toolCalls: List<Map<String, Any>>? = null,
    public var finishReason: String? = "stop",
    delay: Duration = 0.seconds,
) : AbstractResponseSpecification<ChatRequest, ChatResponse>(
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
                toolCalls = toolCalls?.map(::toToolCall),
            )

        return message
    }

    private fun toToolCall(definition: Map<String, Any>): ToolCall =
        ToolCall(
            id =
                definition["id"]?.let {
                    it as? String ?: error("tool call id must be a string")
                },
            type =
                definition["type"]?.let {
                    it as? String ?: error("tool call type must be a string")
                },
            function =
                (definition["function"] as? Map<*, *>)
                    ?.let { function ->
                        FunctionCall(
                            name =
                                function["name"] as? String
                                    ?: error("tool call function.name must be a string"),
                            arguments =
                                when (val arguments = function["arguments"]) {
                                    is JsonObject -> arguments
                                    is Map<*, *> -> arguments.toJsonObject()
                                    else -> error("tool call function.arguments must be an object")
                                },
                        )
                    } ?: error("tool call function must be an object"),
        )

    private fun Map<*, *>.toJsonObject(): JsonObject =
        JsonObject(
            entries.associate { (key, value) ->
                (
                    key as? String
                        ?: error("tool call object keys must be strings")
                ) to value.toJsonElement()
            },
        )

    private fun Any?.toJsonElement(): JsonElement =
        when (this) {
            null -> JsonNull
            is JsonElement -> this
            is String -> JsonPrimitive(this)
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is Map<*, *> -> toJsonObject()
            is List<*> -> JsonArray(map { it.toJsonElement() })
            else -> error("unsupported tool call argument value: $this")
        }
}
