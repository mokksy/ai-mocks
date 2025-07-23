package me.kpavlov.aimocks.ollama.embed

/**
 * Specification for matching embedding requests.
 *
 * This class is used to specify the criteria for matching requests to the embed endpoint.
 * It allows specifying the model, input (string or list of strings), truncate flag, options, and keepAlive.
 *
 * @property model The model to match in the request
 * @property stringInput The string input to match in the request
 * @property stringListInput The list of string inputs to match in the request
 * @property truncate Whether to truncate the input to fit within context length
 * @property options Additional model parameters to match in the request
 * @property keepAlive Controls how long the model will stay loaded into memory
 * @property requestBody The request body to match
 * @property requestBodyString Additional string matchers for the request body
 */
public class OllamaEmbedRequestSpecification {
    public var model: String? = null
    public var stringInput: String? = null
    public var stringListInput: List<String>? = null
    public var truncate: Boolean? = null
    public var options: Map<String, String>? = null
    public var keepAlive: String? = null
    public var requestBody: EmbeddingsRequest? = null
    public val requestBodyString: MutableList<String> = mutableListOf()

    /**
     * Sets the model name criterion for matching embedding requests.
     *
     * @param model The name of the model to match.
     * @return This specification instance for method chaining.
     */
    public fun model(model: String): OllamaEmbedRequestSpecification {
        this.model = model
        return this
    }

    /**
     * Sets a single string input for embedding requests, clearing any existing list input.
     *
     * @param input The string to use as the embedding input.
     * @return This specification instance for method chaining.
     */
    public fun stringInput(input: String): OllamaEmbedRequestSpecification {
        this.stringInput = input
        this.stringListInput = null // Clear the other input type
        return this
    }

    /**
     * Sets the list of string inputs for embedding, clearing any previously set single string input.
     *
     * @param inputs The list of strings to use as input for the embedding request.
     * @return This specification instance for method chaining.
     */
    public fun stringListInput(inputs: List<String>): OllamaEmbedRequestSpecification {
        this.stringListInput = inputs
        this.stringInput = null // Clear the other input type
        return this
    }

    /**
     * Sets whether to truncate the input to fit within the model's context length.
     *
     * @param truncate If true, input will be truncated to fit the context length.
     * @return This specification instance for method chaining.
     */
    public fun truncate(truncate: Boolean): OllamaEmbedRequestSpecification {
        this.truncate = truncate
        return this
    }

    /**
     * Sets additional model parameters for the embedding request specification.
     *
     * @param options A map of model parameter names to their values.
     * @return This specification instance for method chaining.
     */
    public fun options(options: Map<String, String>): OllamaEmbedRequestSpecification {
        this.options = options
        return this
    }

    /**
     * Sets the duration for which the model remains loaded in memory.
     *
     * @param keepAlive The keep-alive duration string.
     * @return This specification instance for method chaining.
     */
    public fun keepAlive(keepAlive: String): OllamaEmbedRequestSpecification {
        this.keepAlive = keepAlive
        return this
    }

    /**
     * Sets the full request body for embedding request matching.
     *
     * @param requestBody The `EmbeddingsRequest` object to use as the request body.
     * @return This specification instance for method chaining.
     */
    public fun requestBody(requestBody: EmbeddingsRequest): OllamaEmbedRequestSpecification {
        this.requestBody = requestBody
        return this
    }

    /**
     * Adds a string matcher to the list of request body matchers for this specification.
     *
     * @param bodyString The string to match against the request body.
     * @return This specification instance for method chaining.
     */
    public fun requestBodyString(bodyString: String): OllamaEmbedRequestSpecification {
        this.requestBodyString.add(bodyString)
        return this
    }
}
