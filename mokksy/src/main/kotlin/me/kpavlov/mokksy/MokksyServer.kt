package me.kpavlov.mokksy

import io.kotest.assertions.failure
import io.kotest.matchers.equalityMatcher
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Head
import io.ktor.http.HttpMethod.Companion.Options
import io.ktor.http.HttpMethod.Companion.Patch
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.http.HttpMethod.Companion.Put
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import kotlinx.coroutines.runBlocking
import org.slf4j.event.Level
import java.util.concurrent.ConcurrentSkipListSet

@Suppress("TooManyFunctions")
public open class MokksyServer(
    private val port: Int = 0,
    private val verbose: Boolean = false,
    wait: Boolean = false,
    configurer: (Application) -> Unit = { },
) {
    private var resolvedPort: Int

    private val server =
        embeddedServer(Netty, port = port) {
            install(SSE)
            install(CallLogging) {
                if (verbose) {
                    this.level = Level.DEBUG
                } else {
                    this.level = Level.INFO
                }
            }

            install(DoubleReceive)

            routing {
                route("{...}") {
                    handle {
                        handleRequest(this@handle, this@embeddedServer, stubs)
                    }
                }
            }
            configurer(this)
        }

    private val stubs = ConcurrentSkipListSet<Stub<*>>()

    init {
        server.start(wait = wait)
        runBlocking {
            resolvedPort =
                server.engine
                    .resolvedConnectors()
                    .first()
                    .port
        }
    }

    /**
     * Retrieves the resolved port on which the server is running.
     *
     * @return The currently configured port number for the server.
     */
    public fun port(): Int = resolvedPort

    /**
     * Creates a `RequestSpecification` with the specified HTTP method and additional configuration
     * defined by the given block, and returns a new `BuildingStep` instance for further customization.
     *
     * @param httpMethod The `HttpMethod` to match for the request specification.
     * @param block A lambda used to configure the `RequestSpecificationBuilder`.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun method(
        httpMethod: HttpMethod,
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> {
        val requestSpec =
            RequestSpecificationBuilder()
                .apply(block)
                .method(equalityMatcher(httpMethod))
                .build()
        return BuildingStep(
            stubs = stubs,
            requestSpecification = requestSpec,
        )
    }

    /**
     * Configures a HTTP GET request specification using the provided block and returns a `BuildingStep`
     * instance for further customization.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun get(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Get, block)

    /**
     * Configures an HTTP POST request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP POST method to define the request
     * specification within the provided lambda.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the POST request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun post(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Post, block)

    /**
     * Configures an HTTP DELETE request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP DELETE method to define the request
     * specification within the provided lambda.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the DELETE request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun delete(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Delete, block)

    /**
     * Configures an HTTP PATCH request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method uses the HTTP PATCH method to define the request
     * specification within the provided lambda.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the PATCH request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun patch(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Patch, block)

    /**
     * Configures an HTTP PUT request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP PUT method to define the request
     * specification within the provided lambda.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the PUT request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun put(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Put, block)

    /**
     * Configures an HTTP HEAD request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP HEAD method to define the request
     * specification within the provided lambda.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the HEAD request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun head(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Head, block)

    /**
     * Configures an HTTP OPTIONS request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method uses the HTTP OPTIONS method to define the request
     * specification within the provided lambda.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the OPTIONS request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun options(
        block: RequestSpecificationBuilder<*>.() -> Unit,
    ): BuildingStep<RequestSpecification> = method(Options, block)

    /**
     * Retrieves a list of all request specifications that have not been matched to any incoming requests.
     * A request is considered unmatched if its match count is zero.
     *
     * @return A list of unmatched request specifications.
     */
    public fun findAllUnmatchedRequests(): List<RequestSpecification> =
        stubs
            .filter {
                it.matchCount() == 0
            }.map { it.requestSpecification }
            .toList()

    /**
     * Resets the match counts for all mappings in the server. Each mapping's match count
     * is set to zero, effectively clearing any record of previous matches.
     *
     * This is useful for resetting the state of the server when reinitializing or performing
     * testing scenarios.
     */
    public fun resetMatchCounts() {
        stubs
            .forEach {
                it.resetMatchCount()
            }
    }

    /**
     * Checks for any unmatched requests by retrieving all request specifications that
     * have not been matched to incoming requests and verifies that the list of unmatched
     * requests is empty.
     *
     * This method ensures that all request mappings have been utilized as expected. If
     * there are unmatched requests, this would indicate that some defined mappings were
     * not triggered during the testing or execution process.
     *
     * Utilizes the `findAllUnmatchedRequests` function to identify unmatched requests and
     * asserts that no unmatched requests exist.
     */
    public fun checkForUnmatchedRequests() {
        val unmatchedRequests = findAllUnmatchedRequests()
        if (unmatchedRequests.isNotEmpty()) {
            failure(
                "The following requests were not matched: ${
                    unmatchedRequests.joinToString {
                        it
                            .toDescription()
                    }
                }",
            )
        }
    }

    /**
     * Shuts down the server by stopping its execution.
     *
     * This method halts any ongoing operations of the server,
     * effectively terminating its current state and releasing any occupied resources.
     * It should be invoked to safely stop the server when it is no longer needed.
     */
    public fun shutdown() {
        server.stop()
    }
}
