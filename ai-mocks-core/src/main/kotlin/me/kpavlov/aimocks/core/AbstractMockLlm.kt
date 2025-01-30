package me.kpavlov.aimocks.core

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
    protected val mokksy: MokksyServer = MokksyServer(port = port, verbose = verbose)

    public fun port(): Int = mokksy.port()

    public fun shutdown() {
        mokksy.shutdown()
    }

    public abstract fun completion(requestSpecification: R.() -> Unit): S

    /**
     * Creates a completion request based on the specified request configuration.
     *
     * Note: This is Java-friendly API
     *
     * @param requestSpecification a Consumer that specifies the configuration for the chat request,
     *                              which is applied to an instance of the request specification.
     * @return a building step that can be used to define the expected response for the completion request.
     */
    public open fun completion(requestSpecification: Consumer<R>): S =
        completion {
            requestSpecification.accept(this)
        }

    public fun verifyNoUnmatchedRequests() {
        mokksy.checkForUnmatchedRequests()
    }
}
