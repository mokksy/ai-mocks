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
     * Specifies the model to match in the request.
     *
     * @param model The model name
     * @return This specification for method chaining
     */
    public fun model(model: String): OllamaEmbedRequestSpecification {
        this.model = model
        return this
    }

    /**
     * Specifies the string input to match in the request.
     * This is used for single string embedding requests.
     *
     * @param input The string input
     * @return This specification for method chaining
     */
    public fun stringInput(input: String): OllamaEmbedRequestSpecification {
        this.stringInput = input
        this.stringListInput = null // Clear the other input type
        return this
    }

    /**
     * Specifies the list of string inputs to match in the request.
     * This is used for multiple string embedding requests.
     *
     * @param inputs The list of string inputs
     * @return This specification for method chaining
     */
    public fun stringListInput(inputs: List<String>): OllamaEmbedRequestSpecification {
        this.stringListInput = inputs
        this.stringInput = null // Clear the other input type
        return this
    }

    /**
     * Specifies whether to truncate the input to fit within context length.
     *
     * @param truncate Whether to truncate the input
     * @return This specification for method chaining
     */
    public fun truncate(truncate: Boolean): OllamaEmbedRequestSpecification {
        this.truncate = truncate
        return this
    }

    /**
     * Specifies additional model parameters to match in the request.
     *
     * @param options The model parameters
     * @return This specification for method chaining
     */
    public fun options(options: Map<String, String>): OllamaEmbedRequestSpecification {
        this.options = options
        return this
    }

    /**
     * Specifies how long the model will stay loaded into memory.
     *
     * @param keepAlive The keep alive duration
     * @return This specification for method chaining
     */
    public fun keepAlive(keepAlive: String): OllamaEmbedRequestSpecification {
        this.keepAlive = keepAlive
        return this
    }

    /**
     * Specifies the request body to match.
     *
     * @param requestBody The request body
     * @return This specification for method chaining
     */
    public fun requestBody(requestBody: EmbeddingsRequest): OllamaEmbedRequestSpecification {
        this.requestBody = requestBody
        return this
    }

    /**
     * Adds a string matcher for the request body.
     *
     * @param bodyString The string matcher
     * @return This specification for method chaining
     */
    public fun requestBodyString(bodyString: String): OllamaEmbedRequestSpecification {
        this.requestBodyString.add(bodyString)
        return this
    }
}
