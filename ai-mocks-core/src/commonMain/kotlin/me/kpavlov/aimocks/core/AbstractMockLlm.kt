package me.kpavlov.aimocks.core

import io.ktor.server.application.log
import me.kpavlov.mokksy.MokksyServer
import java.util.function.Consumer

public abstract class AbstractMockLlm<
    S : LlmBuildingStep<T>,
    R : ChatRequestSpecification,
    T : ChatResponseSpecification<*>,
>(
    port: Int = 0,
    verbose: Boolean = true,
) {
    protected val mokksy: MokksyServer =
        MokksyServer(port = port, verbose = verbose) {
            it.log.info("Running Mokksy with ${it.engine} engine")
        }

    public fun port(): Int = mokksy.port()

    public fun shutdown() {
        mokksy.shutdown()
    }

    public fun completion(requestSpecification: R.() -> Unit): S =
        completion(
            name = null,
            requestSpecification = requestSpecification,
        )

    public abstract fun completion(
        name: String? = null,
        requestSpecification: R.() -> Unit,
    ): S

    /**
     * Creates a completion request based on the specified request configuration.
     *
     * Note: This is Java-friendly API
     *
     * @param name An optional name assigned to the Stub for identification or debugging purposes.
     * @param requestSpecification a Consumer that specifies the configuration for the chat request,
     *                              which is applied to an instance of the request specification.
     * @return a building step that can be used to define the expected response for the completion request.
     */
    @JvmOverloads
    public open fun completion(
        name: String? = null,
        requestSpecification: Consumer<R>,
    ): S =
        completion {
            requestSpecification.accept(this)
        }

    public fun verifyNoUnmatchedRequests() {
        mokksy.checkForUnmatchedRequests()
    }
}
