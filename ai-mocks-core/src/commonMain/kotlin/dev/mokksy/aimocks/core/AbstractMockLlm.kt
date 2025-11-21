package dev.mokksy.aimocks.core

import dev.mokksy.mokksy.ApplicationConfigurer
import dev.mokksy.mokksy.MokksyServer
import dev.mokksy.mokksy.ServerConfiguration
import io.ktor.server.application.log

/**
 * Abstract class representing a mock Language Model (LLM) server.
 * This serves as a base class for creating a mock server used for testing
 * Language Model interactions. It is built upon the [MokksyServer] framework for
 * configuring and managing mock servers.
 *
 * @constructor Initializes the mock server with the specified configuration.
 * @param port The port number to use for the server. Defaults to 0, which allows
 *             the system to select an available port automatically.
 * @param configuration The [ServerConfiguration] instance providing details such
 *                      as verbosity, server name, and content negotiation settings.
 * @param applicationConfigurer An optional [ApplicationConfigurer] function to further customize the
 *                              server application configuration. Default is an
 *                              empty configuration.
 *  @author Konstantin Pavlov
 */
public abstract class AbstractMockLlm(
    port: Int = 0,
    configuration: ServerConfiguration,
    applicationConfigurer: ApplicationConfigurer? = {},
) {
    protected val mokksy: MokksyServer =
        MokksyServer(
            port = port,
            configuration = configuration,
        ) {
            applicationConfigurer?.invoke(this)
            log.info("Running ${configuration.name} with $engine engine")
        }

    /**
     * Returns the port number on which the mock server is running.
     *
     * @return The port number used by the mock server.
     */
    public fun port(): Int = mokksy.port()

    /**
     * Stops the mock LLM server and releases its resources
     * with the specified grace period and timeout.
     *
     * @param gracePeriodMillis The duration in milliseconds for the server
     * to attempt a graceful shutdown. Default is 500 milliseconds.
     * @param timeoutMillis The maximum duration in milliseconds
     * to wait for the shutdown process to complete. Default is 1000 milliseconds.
     */
    @JvmOverloads
    public fun shutdown(
        gracePeriodMillis: Long = 500,
        timeoutMillis: Long = 1000,
    ) {
        mokksy.shutdown(gracePeriodMillis, timeoutMillis)
    }

    public fun verifyNoUnmatchedRequests() {
        mokksy.checkForUnmatchedRequests()
    }

    /**
     * Provides the base URL of the mock server to be provided
     * to a language model client.
     *
     * @return The base URL as a string.
     */
    public open fun baseUrl(): String = mokksy.baseUrl()
}
