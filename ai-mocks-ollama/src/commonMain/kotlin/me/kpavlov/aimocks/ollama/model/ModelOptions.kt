package me.kpavlov.aimocks.ollama.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.mokksy.serializers.StringOrListSerializer

/**
 * Configuration options for model text generation behavior.
 *
 * These parameters control aspects such as repetition penalties, randomness, prediction limits,
 * and stopping criteria for language model output generation.
 *
 * See [Valid Parameters and Values](https://github.com/ollama/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values)
 */
@Serializable
public data class ModelOptions(

    /**
     * The size of the context window used to generate the next token.
     * Default: 4096
     */
    @SerialName("num_ctx")
    val numCtx: Int = 4096,

    /**
     * How far back the model looks to prevent repetition.
     * Default: 64. Set to 0 to disable, or -1 to use the full context window.
     */
    @SerialName("repeat_last_n")
    val repeatLastN: Int? = null,

    /**
     * Strength of repetition penalty.
     * Higher values (e.g., 1.5) penalize repeated tokens more strongly.
     * Default: 1.1
     */
    @SerialName("repeat_penalty")
    val repeatPenalty: Double? = null,

    /**
     * Sampling temperature for generation.
     * Higher values increase randomness; lower values make output more deterministic.
     * Default: 0.8
     */
    val temperature: Double? = null,

    /**
     * Random seed for reproducibility.
     * Using the same seed and prompt produces the same output.
     * Default: 0
     */
    val seed: Int? = null,

    /**
     * Stop sequence(s) that end generation when encountered.
     * If null, no stop condition is applied.
     * Example: "AI assistant:"
     */
    @Serializable(with = StringOrListSerializer::class)
    val stop: List<String>? = null,

    /**
     * Maximum number of tokens to generate.
     * -1 means unlimited (until a stop condition is met).
     * Default: -1
     */
    @SerialName("num_predict")
    val numPredict: Int? = null,

    /**
     * Limits token sampling to the top K most likely tokens.
     * Higher values increase output diversity.
     * Default: 40
     */
    @SerialName("top_k")
    val topK: Int? = null,

    /**
     * Top-p (nucleus) sampling parameter.
     * Tokens are sampled from the smallest set whose cumulative probability exceeds top_p.
     * Default: 0.9
     */
    @SerialName("top_p")
    val topP: Double? = null,

    /**
     * Minimum probability ratio for a token to be considered.
     * Filters out tokens whose probability is too small relative to the most likely token.
     * Default: 0.0
     */
    @SerialName("min_p")
    val minP: Double? = null
)
