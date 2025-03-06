package me.kpavlov.aimocks.core

import io.ktor.server.application.log
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.ServerConfiguration

public abstract class AbstractMockLlm(
    port: Int = 0,
    configuration: ServerConfiguration,
) {
    protected val mokksy: MokksyServer =
        MokksyServer(
            port = port,
            configuration = configuration,
        ) {
            it.log.info("Running Mokksy with ${it.engine} engine")
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
    public abstract fun baseUrl(): String
}
