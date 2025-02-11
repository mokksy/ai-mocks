package me.kpavlov.aimocks.openai

import me.kpavlov.aimocks.core.ChatRequestSpecification

public open class OpenaiChatRequestSpecification(
    public var seed: Int? = null,
) : ChatRequestSpecification() {
    public fun seed(value: Int): ChatRequestSpecification = apply { this.seed = value }
}
