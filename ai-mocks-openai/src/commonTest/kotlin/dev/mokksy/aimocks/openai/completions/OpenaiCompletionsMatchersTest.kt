package dev.mokksy.aimocks.openai.completions

import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.FunctionObject
import dev.mokksy.aimocks.openai.Message
import dev.mokksy.aimocks.openai.Tool
import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import dev.mokksy.aimocks.openai.model.chat.MessageContent
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class OpenaiCompletionsMatchersTest {
    private val weatherToolSchema =
        Json.parseToJsonElement(
            """
            {
              "type": "object",
              "properties": {
                "location": {
                  "type": "string",
                  "description": "The city and state, e.g. San Francisco, CA"
                },
                "unit": {
                  "type": "string"
                }
              },
              "required": ["location"]
            }
            """.trimIndent(),
        )

    private fun requestWithWeatherTool(functionName: String = "get_weather") =
        ChatCompletionRequest(
            messages =
                listOf(
                    Message(
                        role = ChatCompletionRole.USER,
                        content = MessageContent.Text("What's the weather?"),
                    ),
                ),
            model = "gpt-4",
            tools =
                listOf(
                    Tool(
                        function =
                            FunctionObject(
                                name = functionName,
                                parameters = weatherToolSchema,
                            ),
                    ),
                ),
        )

    private val requestWithoutTools =
        ChatCompletionRequest(
            messages =
                listOf(
                    Message(
                        role = ChatCompletionRole.USER,
                        content = MessageContent.Text("Hello"),
                    ),
                ),
            model = "gpt-4",
        )

    @Test
    fun `systemMessageContains matcher should provide correct failure message`() {
        // Given
        val matcher = OpenaiCompletionsMatchers.systemMessageContains("expected content")
        val request =
            ChatCompletionRequest(
                messages =
                    listOf(
                        Message(
                            role = ChatCompletionRole.SYSTEM,
                            content = MessageContent.Text("actual content"),
                        ),
                    ),
                model = "gpt-4",
            )

        // When
        val result = matcher.test(request)

        // Then
        result.passed() shouldBe false
        result.failureMessage() shouldBe "System message should contain \"expected content\""
        result.negatedFailureMessage() shouldBe
            "System message should not contain \"expected content\""
    }

    @Test
    fun `systemMessageContains matcher should pass when content matches`() {
        // Given
        val matcher = OpenaiCompletionsMatchers.systemMessageContains("expected content")
        val request =
            ChatCompletionRequest(
                messages =
                    listOf(
                        Message(
                            role = ChatCompletionRole.SYSTEM,
                            content = MessageContent.Text("This contains expected content here"),
                        ),
                    ),
                model = "gpt-4",
            )

        // When
        val result = matcher.test(request)

        // Then
        result.passed() shouldBe true
    }

    @Test
    fun `userMessageContains matcher should provide correct failure message`() {
        // Given
        val matcher = OpenaiCompletionsMatchers.userMessageContains("expected content")
        val request =
            ChatCompletionRequest(
                messages =
                    listOf(
                        Message(
                            role = ChatCompletionRole.USER,
                            content = MessageContent.Text("actual content"),
                        ),
                    ),
                model = "gpt-4",
            )

        // When
        val result = matcher.test(request)

        // Then
        result.passed() shouldBe false
        result.failureMessage() shouldBe "User message should contain \"expected content\""
        result.negatedFailureMessage() shouldBe
            "User message should not contain \"expected content\""
    }

    @Test
    fun `hasToolWithFunction passes when request has matching tool`() {
        val matcher = OpenaiCompletionsMatchers.hasToolWithFunction("get_weather")

        matcher.test(requestWithWeatherTool()).passed() shouldBe true
    }

    @Test
    fun `hasToolWithFunction fails when function name does not match`() {
        val matcher = OpenaiCompletionsMatchers.hasToolWithFunction("unknown_function")

        val result = matcher.test(requestWithWeatherTool())
        result.passed() shouldBe false
        result.failureMessage() shouldBe
            "Request should have tool with function name \"unknown_function\""
        result.negatedFailureMessage() shouldBe
            "Request should not have tool with function name \"unknown_function\""
    }

    @Test
    fun `hasToolWithFunction fails when request has no tools`() {
        val matcher = OpenaiCompletionsMatchers.hasToolWithFunction("get_weather")

        matcher.test(requestWithoutTools).passed() shouldBe false
    }

    @Test
    fun `toolHasParameter passes when function has named parameter`() {
        val matcher = OpenaiCompletionsMatchers.toolHasParameter("get_weather", "location")

        matcher.test(requestWithWeatherTool()).passed() shouldBe true
    }

    @Test
    fun `toolHasParameter fails when parameter does not exist`() {
        val matcher = OpenaiCompletionsMatchers.toolHasParameter("get_weather", "missing_param")

        val result = matcher.test(requestWithWeatherTool())
        result.passed() shouldBe false
        result.failureMessage() shouldBe
            "Function \"get_weather\" should have parameter \"missing_param\""
        result.negatedFailureMessage() shouldBe
            "Function \"get_weather\" should not have parameter \"missing_param\""
    }

    @Test
    fun `toolHasParameter with description passes when description matches`() {
        val matcher =
            OpenaiCompletionsMatchers.toolHasParameter(
                "get_weather",
                "location",
                "The city and state, e.g. San Francisco, CA",
            )

        matcher.test(requestWithWeatherTool()).passed() shouldBe true
    }

    @Test
    fun `toolHasParameter with description fails when description does not match`() {
        val matcher =
            OpenaiCompletionsMatchers.toolHasParameter(
                "get_weather",
                "location",
                "Wrong description",
            )

        val result = matcher.test(requestWithWeatherTool())
        result.passed() shouldBe false
        result.failureMessage() shouldBe
            "Function \"get_weather\" parameter \"location\" should have description \"Wrong description\""
    }

    @Test
    fun `toolParameterHasType passes when parameter type matches`() {
        val matcher =
            OpenaiCompletionsMatchers.toolParameterHasType("get_weather", "location", "string")

        matcher.test(requestWithWeatherTool()).passed() shouldBe true
    }

    @Test
    fun `toolParameterHasType fails when parameter type does not match`() {
        val matcher =
            OpenaiCompletionsMatchers.toolParameterHasType("get_weather", "location", "integer")

        val result = matcher.test(requestWithWeatherTool())
        result.passed() shouldBe false
        result.failureMessage() shouldBe
            "Function \"get_weather\" parameter \"location\" should have type \"integer\""
        result.negatedFailureMessage() shouldBe
            "Function \"get_weather\" parameter \"location\" should not have type \"integer\""
    }

    @Test
    fun `toolParameterHasType fails when parameter does not exist`() {
        val matcher =
            OpenaiCompletionsMatchers.toolParameterHasType("get_weather", "nonexistent", "string")

        matcher.test(requestWithWeatherTool()).passed() shouldBe false
    }

    @Test
    fun `toolRequiresParameters passes when all specified parameters are required`() {
        val matcher = OpenaiCompletionsMatchers.toolRequiresParameters("get_weather", "location")

        matcher.test(requestWithWeatherTool()).passed() shouldBe true
    }

    @Test
    fun `toolRequiresParameters fails when a parameter is not required`() {
        val matcher =
            OpenaiCompletionsMatchers.toolRequiresParameters("get_weather", "location", "unit")

        val result = matcher.test(requestWithWeatherTool())
        result.passed() shouldBe false
        result.failureMessage() shouldBe
            "Function \"get_weather\" should require parameters: location, unit"
        result.negatedFailureMessage() shouldBe
            "Function \"get_weather\" should not require parameters: location, unit"
    }

    @Test
    fun `toolRequiresParameters fails when request has no tools`() {
        val matcher = OpenaiCompletionsMatchers.toolRequiresParameters("get_weather", "location")

        matcher.test(requestWithoutTools).passed() shouldBe false
    }
}
