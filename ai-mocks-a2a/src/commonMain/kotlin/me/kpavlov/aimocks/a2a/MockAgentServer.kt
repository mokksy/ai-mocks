package me.kpavlov.aimocks.a2a

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.log
import kotlinx.serialization.json.Json
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.ServerConfiguration

public open class MockAgentServer(
    port: Int = 0,
    verbose: Boolean = false,
) {
    private val mokksy: MokksyServer =
        MokksyServer(
            port = port,
            configuration =
                ServerConfiguration(
                    verbose = verbose,
                ) { config ->
                    config.json(
                        Json { ignoreUnknownKeys = true },
                    )
                },
        ) {
            it.log.info("Running Mock A2A Agent with ${it.engine} engine")
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

    public fun card(): CardBuildingStep =
        CardBuildingStep(
            mokksy,
        )

    public fun sendTask(): SendTaskBuildingStep = SendTaskBuildingStep(mokksy)

    public fun getTask(): GetTaskBuildingStep = GetTaskBuildingStep(mokksy)
}
