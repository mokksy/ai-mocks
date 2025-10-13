@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.ollama.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request to list all local models.
 */
@Serializable
public object ListModelsRequest

/**
 * Represents a response from the list models endpoint.
 *
 * @property models The list of available models
 */
@Serializable
public data class ListModelsResponse(
    val models: List<ModelInfo>,
)

/**
 * Represents information about a model.
 *
 * @property name The name of the model
 * @property modified The timestamp when the model was last modified
 * @property size The size of the model in bytes
 * @property digest The digest of the model
 * @property details The details of the model
 */
@Serializable
public data class ModelInfo(
    val name: String,
    val modified: String? = null,
    val size: Long? = null,
    val digest: String? = null,
    val details: ModelDetails? = null,
)

/**
 * Represents detailed information about a model.
 *
 * @property format The format of the model
 * @property family The family of the model
 * @property families The families of the model
 * @property parameterSize The parameter size of the model
 * @property quantizationLevel The quantization level of the model
 */
@Serializable
public data class ModelDetails(
    val format: String? = null,
    val family: String? = null,
    val families: List<String>? = null,
    @SerialName("parameter_size")
    val parameterSize: String? = null,
    @SerialName("quantization_level")
    val quantizationLevel: String? = null,
)

/**
 * Represents a request to show information about a model.
 *
 * @property name The name of the model
 */
@Serializable
public data class ShowModelRequest(
    val name: String,
)

/**
 * Represents a response from the show model endpoint.
 *
 * @property modelfile The contents of the Modelfile
 * @property parameters The parameters of the model
 * @property template The template of the model
 * @property system The system message of the model
 * @property license The license of the model
 */
@Serializable
public data class ShowModelResponse(
    val modelfile: String? = null,
    val parameters: Map<String, String>? = null,
    val template: String? = null,
    val system: String? = null,
    val license: String? = null,
)

/**
 * Represents a request to copy a model.
 *
 * @property source The name of the source model
 * @property destination The name of the destination model
 */
@Serializable
public data class CopyModelRequest(
    val source: String,
    val destination: String,
)

/**
 * Represents a response from the copy model endpoint.
 */
@Serializable
public object CopyModelResponse

/**
 * Represents a request to delete a model.
 *
 * @property name The name of the model
 */
@Serializable
public data class DeleteModelRequest(
    val name: String,
)

/**
 * Represents a response from the delete model endpoint.
 */
@Serializable
public object DeleteModelResponse

/**
 * Represents a request to pull a model.
 *
 * @property name The name of the model
 * @property insecure Whether to allow insecure connections
 * @property stream Whether to stream the response
 */
@Serializable
public data class PullModelRequest(
    val name: String,
    val insecure: Boolean? = null,
    val stream: Boolean = true,
)

/**
 * Represents a response from the pull model endpoint.
 *
 * @property status The status of the pull operation
 * @property digest The digest of the model
 * @property total The total size of the model
 * @property completed The completed size of the model
 */
@Serializable
public data class PullModelResponse(
    val status: String,
    val digest: String? = null,
    val total: Long? = null,
    val completed: Long? = null,
)

/**
 * Represents a request to push a model.
 *
 * @property name The name of the model
 * @property insecure Whether to allow insecure connections
 * @property stream Whether to stream the response
 */
@Serializable
public data class PushModelRequest(
    val name: String,
    val insecure: Boolean? = null,
    val stream: Boolean = true,
)

/**
 * Represents a response from the push model endpoint.
 *
 * @property status The status of the push operation
 * @property digest The digest of the model
 * @property total The total size of the model
 * @property completed The completed size of the model
 */
@Serializable
public data class PushModelResponse(
    val status: String,
    val digest: String? = null,
    val total: Long? = null,
    val completed: Long? = null,
)

/**
 * Represents a request to create a model.
 *
 * @property name The name of the model
 * @property modelfile The contents of the Modelfile
 * @property stream Whether to stream the response
 */
@Serializable
public data class CreateModelRequest(
    val name: String,
    val modelfile: String,
    val stream: Boolean = true,
)

/**
 * Represents a response from the create model endpoint.
 *
 * @property status The status of the create operation
 * @property digest The digest of the model
 * @property total The total size of the model
 * @property completed The completed size of the model
 */
@Serializable
public data class CreateModelResponse(
    val status: String,
    val digest: String? = null,
    val total: Long? = null,
    val completed: Long? = null,
)

/**
 * Represents a request to list running models.
 */
@Serializable
public object ListRunningModelsRequest

/**
 * Represents a response from the list running models endpoint.
 *
 * @property models The list of running models
 */
@Serializable
public data class ListRunningModelsResponse(
    val models: List<RunningModelInfo>,
)

/**
 * Represents information about a running model.
 *
 * @property name The name of the model
 * @property id The ID of the running model
 * @property created The timestamp when the model was started
 */
@Serializable
public data class RunningModelInfo(
    val name: String,
    val id: String,
    val created: String,
)

/**
 * Represents a request to get the version of the Ollama server.
 */
@Serializable
public object VersionRequest

/**
 * Represents a response from the version endpoint.
 *
 * @property version The version of the Ollama server
 */
@Serializable
public data class VersionResponse(
    val version: String,
)
