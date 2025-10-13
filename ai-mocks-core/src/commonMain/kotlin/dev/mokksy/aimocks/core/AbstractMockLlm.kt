package dev.mokksy.aimocks.core

import dev.mokksy.mokksy.ApplicationConfigurer
import dev.mokksy.mokksy.MokksyServer
import dev.mokksy.mokksy.ServerConfiguration
import io.ktor.server.application.log

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

    public fun shutdown() {
        mokksy.shutdown()
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
