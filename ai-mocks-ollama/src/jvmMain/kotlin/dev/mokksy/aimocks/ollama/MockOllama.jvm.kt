package dev.mokksy.aimocks.ollama

import dev.mokksy.aimocks.ollama.chat.OllamaChatBuildingStep
import dev.mokksy.aimocks.ollama.chat.OllamaChatRequestSpecification
import dev.mokksy.aimocks.ollama.embed.OllamaEmbedBuildingStep
import dev.mokksy.aimocks.ollama.embed.OllamaEmbedRequestSpecification
import dev.mokksy.aimocks.ollama.generate.OllamaGenerateBuildingStep
import dev.mokksy.aimocks.ollama.generate.OllamaGenerateRequestSpecification
import java.util.function.Consumer

/**
 * Provides a Java-compatible overload for configuring a mock Ollama chat request using a Consumer.
 *
 * @param name An optional name for the mock configuration.
 * @param block A Consumer that configures the chat request specification.
 * @return A building step for further configuration of the mock chat response.
 */
@JvmOverloads
public fun MockOllama.chat(
    name: String? = null,
    block: Consumer<OllamaChatRequestSpecification>,
): OllamaChatBuildingStep = chat(name) { block.accept(this) }

/**
 * Configures a mock `/api/generate` endpoint using a Java `Consumer` to specify the request criteria.
 *
 * This overload enables Java interoperability for setting up request matching
 * and response configuration for the generate API.
 *
 * @param name An optional name for the mock configuration.
 * @param block A Java `Consumer` that configures the generate request specification.
 * @return A building step for further response configuration.
 */
@JvmOverloads
public fun MockOllama.generate(
    name: String? = null,
    block: Consumer<OllamaGenerateRequestSpecification>,
): OllamaGenerateBuildingStep = generate(name) { block.accept(this) }


/**
 * Provides a Java-compatible overload for configuring a mock `/api/embed` endpoint using a Consumer.
 *
 * Allows Java code to specify the embedding request specification for the mock Ollama embed API.
 *
 * @param name Optional name for the mock configuration.
 * @param block Consumer that configures the embedding request specification.
 * @return A building step for further response configuration.
 */
@JvmOverloads
public fun MockOllama.embed(
    name: String? = null,
    block: Consumer<OllamaEmbedRequestSpecification>,
): OllamaEmbedBuildingStep = embed(name) { block.accept(this) }
