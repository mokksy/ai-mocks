package me.kpavlov.aimocks.ollama.chat

import io.kotest.matchers.string.contain
import me.kpavlov.aimocks.core.ModelRequestSpecification

/**
 * Specification for matching chat completion requests.
 *
 * This class is used to specify the criteria for matching requests to the chat endpoint.
 * It allows specifying the model, messages, tools, and stream flag.
 *
 * @property messages The messages to match in the request
 * @property stream Whether to match streaming requests
 */
public class OllamaChatRequestSpecification : ModelRequestSpecification<ChatRequest>() {
    public var seed: Int? = null
    public var messages: List<Message> = mutableListOf()
    public var stream: Boolean? = null

    override fun systemMessageContains(substring: String) {
        requestMatchesPredicate {
            it.messages
                .filter { message -> message.role == "system" }
                .any { message ->
                    message.content.contains(substring)
                }
        }
    }

    override fun userMessageContains(substring: String) {
        requestMatchesPredicate {
            it.messages
                .filter { message -> message.role == "user" }
                .any { message ->
                    message.content.contains(substring)
                }
        }
    }


    /**
     * Specifies the messages to match in the request.
     *
     * @param messages The list of messages
     * @return This specification for method chaining
     */
    public fun messages(messages: List<Message>): OllamaChatRequestSpecification {
        this.messages = messages
        return this
    }

    /**
     * Specifies whether to match streaming requests.
     *
     * @param stream Whether the request is streaming
     * @return This specification for method chaining
     */
    public fun stream(stream: Boolean): OllamaChatRequestSpecification {
        this.stream = stream
        return this
    }

    /**
     * Adds a string matcher for the request body.
     *
     * @param bodyString The string matcher
     * @return This specification for method chaining
     */
    public fun requestBodyString(bodyString: String): OllamaChatRequestSpecification {
        this.requestBodyString += contain(bodyString)
        return this
    }

    /**
     * Adds a user message to match in the request.
     *
     * @param content The message content
     * @return This specification for method chaining
     */
    public fun userMessage(content: String): OllamaChatRequestSpecification {
        val message = Message(role = "user", content = content)
        this.messages += message
        return this
    }

    /**
     * Adds a system message to match in the request.
     *
     * @param content The message content
     * @return This specification for method chaining
     */
    public fun systemMessage(content: String): OllamaChatRequestSpecification {
        val message = Message(role = "system", content = content)
        this.messages += message
        return this
    }
}
