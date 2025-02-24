package me.kpavlov.aimocks.core

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain

public abstract class ChatRequestSpecification<P>(
    public var temperature: Double? = null,
    public var maxCompletionTokens: Long? = null,
    public var model: String? = null,
    public val requestBody: MutableList<Matcher<P?>> = mutableListOf(),
    public val requestBodyString: MutableList<Matcher<String?>> = mutableListOf(),
) {
    public fun temperature(temperature: Double): ChatRequestSpecification<P> =
        apply { this.temperature = temperature }

    public fun model(model: String): ChatRequestSpecification<P> = apply { this.model = model }

    public fun maxCompletionTokens(value: Long): ChatRequestSpecification<P> =
        apply { this.maxCompletionTokens = value }

    /**
     * Adds a condition to ensure the request body contains the specified substring.
     *
     * @param substring The substring that the request body should contain.
     * @return The current instance of ChatRequestSpecification with the updated condition.
     */
    public fun requestBodyContains(substring: String): ChatRequestSpecification<P> =
        apply {
            requestBodyString += contain(substring)
        }

    /**
     * Specifies that the system/developer message must contain the provided string.
     *
     * @param substring The substring that the system message should contain.
     */
    public abstract fun systemMessageContains(substring: String)

    /**
     * Specifies that the user's message must contain the provided substring.
     *
     * @param substring The substring that the user's message should contain.
     */
    public abstract fun userMessageContains(substring: String)
}
