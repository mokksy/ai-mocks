package me.kpavlov.aimocks.ollama.generate

import me.kpavlov.aimocks.core.ModelRequestSpecification

/**
 * Specification for matching generate completion requests.
 *
 * This class is used to specify the criteria for matching requests to the generate endpoint.
 * It allows specifying the model, prompt, system message, template, and stream flag.
 *
 * @property model The model to match in the request
 * @property template The template to match in the request
 * @property stream Whether to match streaming requests
 * @property requestBody The request body to match
 * @property requestBodyString Additional string matchers for the request body
 */
public class OllamaGenerateRequestSpecification : ModelRequestSpecification<GenerateRequest>() {
    public var template: String? = null
    public var stream: Boolean? = null

    /**
     * Specifies that the system message in the request must contain the given substring.
     *
     * @param substring The substring that must be present in the system message.
     */
    override fun systemMessageContains(substring: String) {
        requestMatchesPredicate { it.system?.contains(substring) == true }
    }

    /**
     * Specifies that the request's prompt must contain the given substring.
     *
     * @param substring The substring that must be present in the prompt.
     */
    override fun userMessageContains(substring: String) {
        requestMatchesPredicate { it.prompt?.contains(substring) == true }
    }

    /**
     * Sets the template to match in the generate request.
     *
     * @param template The template string to match.
     * @return This specification instance for method chaining.
     */
    public fun template(template: String): OllamaGenerateRequestSpecification {
        this.template = template
        return this
    }

    /**
     * Sets the stream flag to indicate whether to match streaming requests.
     *
     * @param stream True to match streaming requests; false otherwise.
     * @return This specification instance for method chaining.
     */
    public fun stream(stream: Boolean): OllamaGenerateRequestSpecification {
        this.stream = stream
        return this
    }
}
