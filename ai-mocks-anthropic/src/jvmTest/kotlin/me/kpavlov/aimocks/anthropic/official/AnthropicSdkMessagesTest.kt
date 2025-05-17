package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Metadata
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.anthropic.anthropic
import org.junit.jupiter.api.Disabled
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class AnthropicSdkMessagesTest : AbstractAnthropicTest() {

    @Disabled("TODO: Should be fixed separately, #195")
    @Test
    fun `Should respond with a message`() {
        val messageIdValue = "msg_" + System.currentTimeMillis()

        anthropic.messages {
            temperature = temperatureValue
            model = modelName
            maxTokens = maxTokensValue
            userId = userIdValue
            topP = 0.42
            topK = 100500
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
                .topP(0.42)
                .topK(100500)
                .temperature(temperatureValue)
                .maxTokens(maxTokensValue)
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
