package me.kpavlov.aimocks.openai.embeddings

import me.kpavlov.aimocks.openai.model.embeddings.CreateEmbeddingsRequest

/**
 * Specification for matching OpenAI embedding requests.
 *
 * This class is used to specify the criteria for matching requests to the OpenAI embeddings endpoint.
 * It allows specifying the model, input (string or list of strings), dimensions, encoding format, and user ID.
 *
 * @property model The model ID to match in the request
 * @property stringInput The string input to match in the request
 * @property stringListInput The list of string inputs to match in the request
 * @property dimensions The number of dimensions the resulting output embeddings should have
 * @property encodingFormat The format to return the embeddings in
 * @property user A unique identifier representing your end-user
 * @property requestBody The request body to match
 * @property requestBodyString Additional string matchers for the request body
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create">OpenAI Embeddings API</a>
 */
public class OpenaiEmbedRequestSpecification {
    public var model: String? = null
    public var stringInput: String? = null
    public var stringListInput: List<String>? = null
    public var dimensions: Int? = null

    public var encodingFormat: String? = null
    public var user: String? = null
    public var requestBody: CreateEmbeddingsRequest? = null
    public val requestBodyString: MutableList<String> = mutableListOf()

    /**
     * Sets the model ID criterion for matching embedding requests.
     *
     * @param model The ID of the model to use for generating embeddings.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-model">model parameter</a>
     */
    public fun model(model: String): OpenaiEmbedRequestSpecification {
        this.model = model
        return this
    }

    /**
     * Sets a single string input for embedding generation, clearing any existing list input.
     *
     * @param input The input text to embed, encoded as a string.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-input">input parameter</a>
     */
    public fun stringInput(input: String): OpenaiEmbedRequestSpecification {
        this.stringInput = input
        this.stringListInput = null // Clear the other input type
        return this
    }

    /**
     * Sets the list of string inputs for embedding generation, clearing any previously set single string input.
     *
     * @param inputs An array of strings to embed, encoded as a list of strings.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-input">input parameter</a>
     */
    public fun stringListInput(inputs: List<String>): OpenaiEmbedRequestSpecification {
        this.stringListInput = inputs
        this.stringInput = null // Clear the other input type
        return this
    }

    /**
     * Sets the number of dimensions the resulting output embeddings should have.
     *
     * @param value The number of dimensions for the output embeddings. Only supported in text-embedding-3 and later models.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-dimensions">dimensions parameter</a>
     */
    public fun dimensions(value: Int?): OpenaiEmbedRequestSpecification {
        this.dimensions = value
        return this
    }

    /**
     * Sets the encoding format for the embedding request.
     *
     * @param value The encoding format as a string.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-encoding_format">specification</a>
     */
    public fun encodingFormat(value: String): OpenaiEmbedRequestSpecification {
        this.encodingFormat = value
        return this
    }

    /**
     * Sets the user identifier for the embedding request.
     *
     * @param value A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create#embeddings-create-user">user parameter</a>
     */
    public fun user(value: String): OpenaiEmbedRequestSpecification {
        this.user = value
        return this
    }

    /**
     * Sets the full request body for embedding request matching.
     *
     * @param requestBody The [CreateEmbeddingsRequest] object to match against the request body.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create">OpenAI Embeddings API</a>
     */
    public fun requestBody(requestBody: CreateEmbeddingsRequest): OpenaiEmbedRequestSpecification {
        this.requestBody = requestBody
        return this
    }

    /**
     * Adds a string matcher to the list of request body matchers for this specification.
     *
     * @param bodyString The string pattern to match against the serialized request body.
     * @return This specification instance for method chaining.
     */
    public fun requestBodyString(bodyString: String): OpenaiEmbedRequestSpecification {
        this.requestBodyString.add(bodyString)
        return this
    }
}
