package me.kpavlov.aimocks.ollama.generate

import me.kpavlov.aimocks.core.ModelRequestSpecification

/**
 * Specification for matching generate completion requests.
 *
 * This class is used to specify the criteria for matching requests to the generate endpoint.
 * It allows specifying the model, prompt, system message, template, and stream flag.
 *
 * @property model The model to match in the request
 * @property prompt The prompt to match in the request
 * @property system The system message to match in the request
 * @property template The template to match in the request
 * @property stream Whether to match streaming requests
 * @property requestBody The request body to match
 * @property requestBodyString Additional string matchers for the request body
 */
public class OllamaGenerateRequestSpecification : ModelRequestSpecification<GenerateRequest>() {
    public var prompt: String? = null
    public var system: String? = null
    public var template: String? = null
    public var stream: Boolean? = null

    override fun systemMessageContains(substring: String) {
        requestMatchesPredicate { it.system?.contains(substring) == true }
    }

    override fun userMessageContains(substring: String) {
        requestMatchesPredicate { it.prompt?.contains(substring) == true }
    }

    /**
     * Specifies the prompt to match in the request.
     *
     * @param prompt The prompt text
     * @return This specification for method chaining
     */
    public fun prompt(prompt: String): OllamaGenerateRequestSpecification {
        this.prompt = prompt
        return this
    }

    /**
     * Specifies the system message to match in the request.
     *
     * @param system The system message
     * @return This specification for method chaining
     */
    public fun system(system: String): OllamaGenerateRequestSpecification {
        this.system = system
        return this
    }

    /**
     * Specifies the template to match in the request.
     *
     * @param template The template
     * @return This specification for method chaining
     */
    public fun template(template: String): OllamaGenerateRequestSpecification {
        this.template = template
        return this
    }

    /**
     * Specifies whether to match streaming requests.
     *
     * @param stream Whether the request is streaming
     * @return This specification for method chaining
     */
    public fun stream(stream: Boolean): OllamaGenerateRequestSpecification {
        this.stream = stream
        return this
    }
}
