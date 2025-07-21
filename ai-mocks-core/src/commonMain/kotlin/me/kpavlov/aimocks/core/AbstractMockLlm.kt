package me.kpavlov.aimocks.core

import io.ktor.server.application.log
import me.kpavlov.mokksy.ApplicationConfigurer
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.ServerConfiguration

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

    public fun port(): Int = mokksy.port()

    public fun shutdown() {
        mokksy.shutdown()
    }

    public fun verifyNoUnmatchedRequests() {
        mokksy.checkForUnmatchedRequests()
    }

    /**
     * Provides the base URL of the mock server to be provided
     * to language model client.
     *
     * @return The base URL as a string.
     */
    public open fun baseUrl(): String = "http://localhost:${port()}"
}
