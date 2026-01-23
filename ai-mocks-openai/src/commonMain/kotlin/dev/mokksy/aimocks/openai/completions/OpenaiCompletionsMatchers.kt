package dev.mokksy.aimocks.openai.completions

import dev.mokksy.aimocks.core.json.schema.SchemaHelper
import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Utility object providing custom matchers for testing and validating attributes of
 * `ChatCompletionRequest` instances. These matchers focus on checking the contents of the
 * messages within the `ChatCompletionRequest` for specific roles such as SYSTEM, DEVELOPER, or USER.
 * @author Konstantin Pavlov
 */
internal object OpenaiCompletionsMatchers {
    fun systemMessageContains(string: String): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.messages
                            .find {
                                it.role == ChatCompletionRole.SYSTEM ||
                                    it.role == ChatCompletionRole.DEVELOPER
                            }?.content
                            ?.contains(string) == true,
                    { "System message should contain \"$string\"" },
                    { "System message should not contain \"$string\"" },
                )

            override fun toString(): String = "System message should contain \"$string\""
        }

    fun userMessageContains(string: String): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.messages
                            .find { it.role == ChatCompletionRole.USER }
                            ?.content
                            ?.contains(string) == true,
                    { "User message should contain \"$string\"" },
                    { "User message should not contain \"$string\"" },
                )

            override fun toString(): String = "User message should contain \"$string\""
        }

    /**
     * Matches requests that have a tool with the specified function name.
     *
     * @param functionName The name of the function to match
     * @return A matcher that checks if the request contains a tool with the specified function name
     */
    fun hasToolWithFunction(functionName: String): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult =
                MatcherResult.Companion(
                    value?.tools?.any { tool ->
                        tool.function.name == functionName
                    } == true,
                    { "Request should have tool with function name \"$functionName\"" },
                    { "Request should not have tool with function name \"$functionName\"" },
                )

            override fun toString(): String =
                "Request should have tool with function name \"$functionName\""
        }

    /**
     * Matches requests where a tool's function has a parameter with the specified name.
     *
     * @param functionName The name of the function
     * @param parameterName The name of the parameter to check
     * @return A matcher that checks if the specified function has the named parameter
     */
    fun toolHasParameter(
        functionName: String,
        parameterName: String,
    ): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult {
                val tool = value?.tools?.find { it.function.name == functionName }
                val schema = tool?.function?.parameters?.let { SchemaHelper.parseSchema(it) }
                val hasParameter =
                    schema?.let { SchemaHelper.hasProperty(it, parameterName) } == true

                return MatcherResult.Companion(
                    hasParameter,
                    { "Function \"$functionName\" should have parameter \"$parameterName\"" },
                    { "Function \"$functionName\" should not have parameter \"$parameterName\"" },
                )
            }

            override fun toString(): String =
                "Function \"$functionName\" should have parameter \"$parameterName\""
        }

    /**
     * Matches requests where a tool's function has a parameter with the specified name and description.
     *
     * @param functionName The name of the function
     * @param parameterName The name of the parameter to check
     * @param description The expected description of the parameter
     * @return A matcher that checks if the specified function has the named parameter with the given description
     */
    fun toolHasParameter(
        functionName: String,
        parameterName: String,
        description: String,
    ): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult {
                val tool = value?.tools?.find { it.function.name == functionName }
                val schema = tool?.function?.parameters?.let { SchemaHelper.parseSchema(it) }
                val actualDescription =
                    schema?.let { SchemaHelper.getPropertyDescription(it, parameterName) }
                val matches = actualDescription == description

                return MatcherResult.Companion(
                    matches,
                    {
                        "Function \"$functionName\" parameter \"$parameterName\" " +
                            "should have description \"$description\""
                    },
                    {
                        "Function \"$functionName\" parameter \"$parameterName\" " +
                            "should not have description \"$description\""
                    },
                )
            }

            override fun toString(): String =
                "Function \"$functionName\" parameter \"$parameterName\" should have description \"$description\""
        }

    /**
     * Matches requests where a tool's function parameter has the specified type.
     *
     * @param functionName The name of the function
     * @param parameterName The name of the parameter
     * @param expectedType The expected type (e.g., "string", "integer", "number", "boolean", "object", "array")
     * @return A matcher that checks if the parameter has the expected type
     */
    fun toolParameterHasType(
        functionName: String,
        parameterName: String,
        expectedType: String,
    ): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult {
                val tool = value?.tools?.find { it.function.name == functionName }
                val schema = tool?.function?.parameters?.let { SchemaHelper.parseSchema(it) }
                val actualTypes = schema?.let { SchemaHelper.getPropertyType(it, parameterName) }
                val hasCorrectType = actualTypes?.contains(expectedType) == true

                return MatcherResult.Companion(
                    hasCorrectType,
                    {
                        "Function \"$functionName\" parameter \"$parameterName\" should have type \"$expectedType\""
                    },
                    {
                        "Function \"$functionName\" parameter \"$parameterName\" should not have type \"$expectedType\""
                    },
                )
            }

            override fun toString(): String =
                "Function \"$functionName\" parameter \"$parameterName\" should have type \"$expectedType\""
        }

    /**
     * Matches requests where a tool's function schema requires specific parameters.
     *
     * @param functionName The name of the function
     * @param requiredParams The list of parameter names that should be required
     * @return A matcher that checks if all specified parameters are required
     */
    fun toolRequiresParameters(
        functionName: String,
        vararg requiredParams: String,
    ): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult {
                val tool = value?.tools?.find { it.function.name == functionName }
                val schema = tool?.function?.parameters?.let { SchemaHelper.parseSchema(it) }
                val hasAllRequired =
                    schema?.let {
                        SchemaHelper.hasAllRequiredProperties(
                            schema = it,
                            propertyNames = arrayOf(elements = requiredParams),
                        )
                    } == true

                return MatcherResult.Companion(
                    hasAllRequired,
                    {
                        "Function \"$functionName\" should require parameters: ${
                            requiredParams.joinToString(
                                ", ",
                            )
                        }"
                    },
                    {
                        "Function \"$functionName\" should not require parameters: ${
                            requiredParams.joinToString(
                                ", ",
                            )
                        }"
                    },
                )
            }

            override fun toString(): String =
                "Function \"$functionName\" should require parameters: ${
                    requiredParams.joinToString(
                        ", ",
                    )
                }"
        }
}
