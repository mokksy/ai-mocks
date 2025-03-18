package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Metadata
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.anthropic.anthropic
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class AnthropicSdkMessagesTest : AbstractAnthropicTest() {
    @Test
    fun `Should respond with a message`() {
        val messageIdValue = "msg_" + System.currentTimeMillis()

        anthropic.messages {
            temperature = temperatureValue
            model = modelName
            maxCompletionTokens = maxCompletionTokensValue
            userId = userIdValue
            systemMessageContains("witch")
            userMessageContains("say 'He-he!'")
        } responds {
            messageId = messageIdValue
            assistantContent = "He-he!"
            delay = 50.milliseconds
        }

        val params =
            MessageCreateParams
                .builder()
                .model(modelName)
                .temperature(temperatureValue)
                .maxTokens(maxCompletionTokensValue)
                .metadata(Metadata.builder().userId(userIdValue).build())
                .system("You are witch")
                .addUserMessage("Just say 'He-he!' and nothing else")
                .model(modelName)
                .build()

        println("params = $params")

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

        text shouldBe "He-he!"
    }
}
