package me.kpavlov.aimocks.openai.completions

import me.kpavlov.aimocks.core.AbstractInferenceRequestSpecification
import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest

/**
 * Represents the specification for an OpenAI chat completion request.
 *
 * Extends [ModelRequestSpecification] specifically for configuring and validating
 * parameters related to `ChatCompletionRequest` objects. This includes overriding
 * methods to add matchers for specific conditions such as ensuring messages
 * from the system or user contain specified substrings.
 *
 * @constructor Creates an instance with optional parameters for initializing configuration.
 * @property seed An optional seed value for deterministic behavior in chat completions.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Create Chat Completion</a>
 * @author Konstantin Pavlov
 */
public open class OpenaiChatCompletionRequestSpecification(
    public var seed: Int? = null,
) : AbstractInferenceRequestSpecification<ChatCompletionRequest>() {
    /**
     * Sets the seed value for deterministic behavior in chat completions.
     *
     * @param value The seed value to use for reproducible results.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-seed">seed parameter</a>
     */
    public fun seed(value: Int): OpenaiChatCompletionRequestSpecification =
        apply {
            this.seed = value
        }

    override fun systemMessageContains(substring: String) {
        requestBody.add(OpenaiCompletionsMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(OpenaiCompletionsMatchers.userMessageContains(substring))
    }

    /**
     * Adds a matcher to verify that the request includes a tool with the specified function name.
     *
     * @param functionName The name of the function to match
     */
    public fun hasToolWithFunction(functionName: String) {
        requestBody.add(OpenaiCompletionsMatchers.hasToolWithFunction(functionName))
    }

    /**
     * Adds a matcher to verify that a tool's function has a parameter with the specified name.
     *
     * @param functionName The name of the function
     * @param parameterName The name of the parameter to check
     */
    public fun toolHasParameter(
        functionName: String,
        parameterName: String,
    ) {
        requestBody.add(OpenaiCompletionsMatchers.toolHasParameter(functionName, parameterName))
    }

    /**
     * Adds a matcher to verify that a tool's function has a parameter with the specified name and description.
     *
     * @param functionName The name of the function
     * @param parameterName The name of the parameter to check
     * @param description The expected description of the parameter
     */
    public fun toolHasParameter(
        functionName: String,
        parameterName: String,
        description: String,
    ) {
        requestBody.add(OpenaiCompletionsMatchers.toolHasParameter(functionName, parameterName, description))
    }

    /**
     * Adds a matcher to verify that a tool's function parameter has the specified type.
     *
     * @param functionName The name of the function
     * @param parameterName The name of the parameter
     * @param expectedType The expected type (e.g., "string", "integer", "number", "boolean", "object", "array")
     */
    public fun toolParameterHasType(
        functionName: String,
        parameterName: String,
        expectedType: String,
    ) {
        requestBody.add(
            OpenaiCompletionsMatchers.toolParameterHasType(functionName, parameterName, expectedType),
        )
    }

    /**
     * Adds a matcher to verify that a tool's function requires specific parameters.
     *
     * @param functionName The name of the function
     * @param requiredParams The parameter names that should be required
     */
    public fun toolRequiresParameters(
        functionName: String,
        vararg requiredParams: String,
    ) {
        requestBody.add(OpenaiCompletionsMatchers.toolRequiresParameters(functionName, *requiredParams))
    }
}
