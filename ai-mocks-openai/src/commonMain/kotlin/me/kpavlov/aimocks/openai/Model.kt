@file:OptIn(ExperimentalSerializationApi::class)

package me.kpavlov.aimocks.openai

import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class Chunk(
    val id: String,
    /**
     * Always "chat.completion.chunk"
     */
    @SerialName("object")
    @EncodeDefault(ALWAYS)
    val objectType: String = "chat.completion.chunk",
    val created: Long,
    val model: String,
    @SerialName("service_tier")
    val serviceTier: String? = null,
    @SerialName("system_fingerprint")
    val systemFingerprint: String,
    val usage: Usage? = null,
    val choices: List<Choice>,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class Choice(
    val index: Int,
    @EncodeDefault(NEVER)
    val delta: Delta? = null,
    @EncodeDefault(NEVER)
    val message: Message? = null,
    @EncodeDefault(ALWAYS)
    val logprobs: String? = null,
    @EncodeDefault(ALWAYS)
    @SerialName("finish_reason")
    val finishReason: String? = null,
)

@Serializable
public data class Delta(
    val role: String? = null,
    val content: String? = null,
)

@Serializable
public data class ChatResponse(
    val id: String,
    @EncodeDefault(ALWAYS)
    @SerialName("object")
    val objectType: String = "chat.completion",
    val created: Long,
    val model: String,
    @SerialName("service_tier")
    val serviceTier: String? = null,
    @SerialName("system_fingerprint")
    val systemFingerprint: String? = null,
    val usage: Usage,
    val choices: List<Choice>,
)

@Serializable
public data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("completion_tokens_details")
    val completionTokensDetails: CompletionTokensDetails,
    @SerialName("prompt_tokens_details")
    val promptTokensDetails: TokenDetails? = null,
)

@Serializable
public data class CompletionTokensDetails(
    @SerialName("reasoning_tokens")
    val reasoningTokens: Int,
    @SerialName("accepted_prediction_tokens")
    val acceptedPredictionTokens: Int,
    @SerialName("rejected_prediction_tokens")
    val rejectedPredictionTokens: Int,
)

@Serializable
public data class TokenDetails(
    @SerialName("cached_tokens")
    val cachedTokens: Int? = null,
)

@Serializable
internal data class Metadata(
    val tags: Map<String, String>? = null,
)

@Serializable
internal data class ChatCompletionRequest(
    val messages: List<Message>,
    val model: String,
    val store: Boolean = false,
    @SerialName("reasoning_effort")
    val reasoningEffort: String = "medium",
    val metadata: Metadata? = null,
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,
    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = 0.0,
    @SerialName("response_format")
    val responseFormat: ResponseFormat? = null,
    val temperature: Double = 1.0,
)

@Serializable
public data class Message(
    val role: String,
    val content: String,
)

// Add new parameters like JSON Schema support if required
@Serializable
internal data class ResponseFormat(
    val type: String,
    @SerialName("json_schema")
    val jsonSchema: Map<String, @Contextual Any>? = null,
)
