package me.kpavlov.aimocks.core

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain

public open class ChatRequestSpecification<P>(
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

    public fun requestBodyContains(s: String): ChatRequestSpecification<P> =
        apply {
            requestBodyString += contain(s)
        }
}
