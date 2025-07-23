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

    /**
     * Adds a predicate to match requests where any system message contains the specified substring.
     *
     * @param substring The substring to search for within the content of system messages.
     */
    override fun systemMessageContains(substring: String) {
        requestMatchesPredicate {
            it.messages
                .filter { message -> message.role == "system" }
                .any { message ->
                    message.content.contains(substring)
                }
        }
    }

    /**
     * Adds a predicate to match requests where any user message contains the specified substring.
     *
     * @param substring The substring to search for within user message contents.
     */
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
     * Sets the list of messages to match in the chat request.
     *
     * @param messages The messages to use as matching criteria.
     * @return This specification instance for method chaining.
     */
    public fun messages(messages: List<Message>): OllamaChatRequestSpecification {
        this.messages = messages
        return this
    }

    /**
     * Sets the streaming flag for the request specification.
     *
     * @param stream If true, matches streaming requests; if false, matches non-streaming requests.
     * @return This specification instance for method chaining.
     */
    public fun stream(stream: Boolean): OllamaChatRequestSpecification {
        this.stream = stream
        return this
    }

    /**
     * Adds a matcher that checks if the request body contains the specified string.
     *
     * @param bodyString The substring to match within the request body.
     * @return This specification instance for method chaining.
     */
    public fun requestBodyString(bodyString: String): OllamaChatRequestSpecification {
        this.requestBodyString += contain(bodyString)
        return this
    }

    /**
     * Appends a user message with the specified content to the list of messages to match in the request.
     *
     * @param content The content of the user message to add.
     * @return This specification instance for method chaining.
     */
    public fun userMessage(content: String): OllamaChatRequestSpecification {
        val message = Message(role = "user", content = content)
        this.messages += message
        return this
    }

    /**
     * Appends a system message with the specified content to the list of messages to match in the request.
     *
     * @param content The content of the system message to add.
     * @return This specification instance for method chaining.
     */
    public fun systemMessage(content: String): OllamaChatRequestSpecification {
        val message = Message(role = "system", content = content)
        this.messages += message
        return this
    }
}
