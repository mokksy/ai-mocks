package me.kpavlov.aimocks.core

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain

public open class ChatRequestSpecification(
    public var temperature: Double? = null,
    public var maxCompletionTokens: Long? = null,
    public var model: String? = null,
    public val requestBody: MutableList<Matcher<String>> = mutableListOf<Matcher<String>>(),
) {
    public fun temperature(temperature: Double): ChatRequestSpecification =
        apply { this.temperature = temperature }

    public fun model(model: String): ChatRequestSpecification = apply { this.model = model }

    public fun maxCompletionTokens(value: Long): ChatRequestSpecification =
        apply { this.maxCompletionTokens = value }

    public fun requestBodyContains(s: String) {
        requestBody += contain(s)
    }
}
