package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.MessageCreateParams
import com.anthropic.models.Metadata
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.anthropic.anthropic
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class MessagesTest : AbstractAnthropicTest() {
    @Test
    fun `Should respond`() {
        val messageIdValue = "msg_" + System.currentTimeMillis()

        anthropic.messages {
            temperature = 0.42
            model = "claude-3-7-sonnet-latest"
            maxCompletionTokens = 100
            userId = userIdValue
            systemMessageContains("helpful assistant #$userIdValue")
            userMessageContains("say 'Hello!'")
        } responds {
            messageId = messageIdValue
            assistantContent = "Hello"
            delay = 50.milliseconds
        }

        val params =
            MessageCreateParams
                .builder()
                .temperature(0.42)
                .maxTokens(100)
                .metadata(Metadata.builder().userId(userIdValue).build())
                .system("You are a helpful assistant #$userIdValue")
                .addUserMessage("Just say 'Hello!' and nothing else")
                .model("claude-3-7-sonnet-latest")
                .build()

        val result =
            client
                .messages()
                .create(params)

        val message = result.validate()

        message.id() shouldBe messageIdValue
        val text =
            message
                .content()
                .mapNotNull { it.text().getOrNull() }
                .map { it.text() }
                .first()

        text shouldBe "Hello"
    }
}
