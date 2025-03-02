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
}
