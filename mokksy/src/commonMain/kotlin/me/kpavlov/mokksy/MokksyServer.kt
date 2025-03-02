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
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import kotlinx.coroutines.runBlocking
import me.kpavlov.mokksy.request.RequestSpecification
import me.kpavlov.mokksy.request.RequestSpecificationBuilder
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.reflect.KClass

internal expect fun createEmbeddedServer(
    host: String = "0.0.0.0",
    port: Int,
    configuration: ServerConfiguration,
    module: Application.() -> Unit,
): EmbeddedServer<
    out ApplicationEngine,
    out ApplicationEngine.Configuration,
>

internal expect fun configureContentNegotiation(config: ContentNegotiationConfig)

/**
 * Represents an embedded mock server capable of handling various HTTP requests and responses for testing purposes.
 * Provides functionality to configure request specifications for different HTTP methods and manage request matching.
 *
 * @constructor Initializes the server with the specified parameters and starts it.
 * @param port The port number on which the server will run. Defaults to 0 (randomly assigned port).
 * @param configuration Server configuration options
 * @param wait Determines whether the server startup process should block the current thread. Defaults to false.
 * @param configurer A lambda function for setting custom configurations for the server's application module.
 */
@Suppress("TooManyFunctions")
public open class MokksyServer(
    port: Int = 0,
    host: String = "0.0.0.0",
    configuration: ServerConfiguration,
    wait: Boolean = false,
    configurer: (Application) -> Unit = {},
) {
    /**
     *  @constructor Initializes the server with the specified parameters and starts it.
     *  @param port The port number on which the server will run. Defaults to 0 (randomly assigned port).
     *  @param verbose A flag indicating whether detailed logs should be printed. Defaults to false.
     *  @param wait Determines whether the server startup process should block the current thread. Defaults to false.
     *  @param configurer A lambda function for setting custom configurations for the server's application module.
     */
    public constructor(
        port: Int = 0,
        host: String = "0.0.0.0",
        verbose: Boolean = false,
        configurer: (Application) -> Unit = {},
    ) : this(
        port = port,
        host = host,
        configuration = ServerConfiguration(verbose = verbose),
        wait = false,
        configurer = configurer,
    )

    private var resolvedPort: Int

    private val server =
        createEmbeddedServer(
            host = host,
            port = port,
            configuration = configuration,
        ) {
            install(SSE)

            install(DoubleReceive)

            install(ContentNegotiation) {
                configureContentNegotiation(this)
            }

            routing {
                route("{...}") {
                    handle {
                        handleRequest(
                            context = this@handle,
                            application = this@createEmbeddedServer,
                            stubs = stubs,
                            configuration = configuration,
                        )
                    }
                }
            }
            configurer(this)
        }

    private val stubs = ConcurrentSkipListSet<Stub<*, *>>()

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
     * Registers a stub in the collection of stubs.
     * Ensures that duplicates are not allowed.
     *
     * @param stub The stub instance to be added to the collection.
     */
    private fun registerStub(stub: Stub<*, *>) {
        val added = stubs.add(stub)
        assert(added) { "Duplicate stub detected: $stub" }
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
     * * @param name An optional name assigned to the Stub for identification or debugging purposes.
     * @param httpMethod The `HttpMethod` to match for the request specification.
     *  @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder`.
     * @return A [BuildingStep] instance initialized with the generated request specification.
     */
    public fun <P : Any> method(
        configuration: StubConfiguration,
        httpMethod: HttpMethod,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> {
        val requestSpec =
            RequestSpecificationBuilder<P>(requestType)
                .apply(block)
                .method(equalityMatcher(httpMethod))
                .build()

        return BuildingStep<P>(
            configuration = configuration,
            requestSpecification = requestSpec,
            registerStub = this::registerStub,
            requestType = requestType,
        )
    }

    /**
     * Creates a building step for a stub configuration with the specified parameters.
     *
     * @param name An optional name for the stub configuration.
     * @param httpMethod The HTTP method associated with the request.
     * @param requestType The class type of the request payload.
     * @param block A block for specifying request details using a RequestSpecificationBuilder.
     * @return A BuildingStep instance configured with the provided parameters.
     */
    public fun <P : Any> method(
        name: String? = null,
        httpMethod: HttpMethod,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name = name),
            httpMethod = httpMethod,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP GET request specification using the provided block and returns a `BuildingStep`
     * instance for further customization.
     *
     * @param P type of the request payload
     * @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> get(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Get,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP GET request specification using the provided block and returns a `BuildingStep`
     * instance for further customization.
     *
     * @param P type of the request payload
     * @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> get(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Get,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures HTTP GET request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun get(block: RequestSpecificationBuilder<String>.() -> Unit): BuildingStep<String> =
        this.get<String>(
            name = null,
            requestType = String::class,
            block = block,
        )

    /**
     * Configures HTTP GET request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param configuration The configuration to be used for the request.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A [BuildingStep] instance initialized with the generated request specification.
     */
    public fun get(
        configuration: StubConfiguration,
        block: RequestSpecificationBuilder<String>.() -> Unit,
    ): BuildingStep<String> =
        this.get<String>(
            configuration = configuration,
            requestType = String::class,
            block = block,
        )

    /**
     * Configures an HTTP POST request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP POST method to define the request
     * specification within the provided lambda.
     *
     * @param P type of the request payload.
     * @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the POST request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> post(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Post,
            requestType = requestType,
            block = block,
        )

    /**
     * Sends a POST request using the provided configuration,
     * request type, and block to define the request specification.
     *
     * @param configuration The configuration settings for the request, including endpoint and other details.
     * @param requestType The class type of the request body.
     * @param block A lambda function to define the specifics of the request, such as headers, query parameters, etc.
     * @return A [BuildingStep] instance representing the constructed POST request.
     */
    public fun <P : Any> post(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Post,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP POST request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun post(block: RequestSpecificationBuilder<String>.() -> Unit): BuildingStep<String> =
        this.post(name = null, requestType = String::class, block = block)

    /**
     * Configures an HTTP DELETE request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP DELETE method to define the request
     * specification within the provided lambda.
     *
     * @param P type of the request payload.
     * @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the DELETE request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> delete(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Delete,
            requestType = requestType,
            block = block,
        )

    /**
     * Executes an HTTP DELETE request with the specified configuration and request type.
     *
     * @param configuration The configuration settings for the request.
     * @param requestType The class of the request type that will be processed.
     * @param block A lambda to configure the request specification for the DELETE request.
     * @return A BuildingStep instance representing the configured DELETE request.
     */
    public fun <P : Any> delete(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Delete,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP DELETE request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun delete(block: RequestSpecificationBuilder<String>.() -> Unit): BuildingStep<String> =
        this.delete(name = null, requestType = String::class, block = block)

    /**
     * Configures an HTTP PATCH request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method uses the HTTP PATCH method to define the request
     * specification within the provided lambda.
     *
     * @param P type of the request payload.
     *  @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the PATCH request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> patch(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Patch,
            requestType = requestType,
            block = block,
        )

    /**
     * Builds and returns a BuildingStep for a PATCH HTTP request with the provided configuration, request type,
     * and custom request specification block.
     *
     * @param configuration The stub configuration that contains the setup details for the request.
     * @param requestType The KClass type of the request body.
     * @param block A lambda block to define the request specification.
     * @return A [BuildingStep] instance representing the constructed PATCH request.
     */
    public fun <P : Any> patch(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Patch,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP PATCH request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun patch(block: RequestSpecificationBuilder<String>.() -> Unit): BuildingStep<String> =
        this.patch(name = null, requestType = String::class, block = block)

    /**
     * Configures an HTTP PUT request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP PUT method to define the request
     * specification within the provided lambda.
     *
     * @param P type of the request payload
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the PUT request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> put(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Put,
            requestType = requestType,
            block = block,
        )

    /**
     * Executes an HTTP PUT request with the provided configuration and request specifications.
     *
     * @param configuration The stub configuration to be used for the request.
     * @param requestType The class type of the request payload.
     * @param block A configuration block to build the request specifications.
     * @return A [BuildingStep] instance for further processing of the request.
     */
    public fun <P : Any> put(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Put,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP PUT request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun put(block: RequestSpecificationBuilder<String>.() -> Unit): BuildingStep<String> =
        this.put(name = null, requestType = String::class, block = block)

    /**
     * Configures an HTTP HEAD request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method utilizes the HTTP HEAD method to define the request
     * specification within the provided lambda.
     *
     * @param P type of the request payload
     *  @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the HEAD request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> head(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Head,
            requestType = requestType,
            block = block,
        )

    /**
     * Constructs a HEAD HTTP request with the provided configuration, request type, and specification block.
     *
     * @param configuration The configuration for the stubbed request.
     * @param requestType The class type of the request payload.
     * @param block A lambda that specifies the request parameters.
     * @return A [BuildingStep] instance representing the constructed request.
     */
    public fun <P : Any> head(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Head,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP HEAD request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun head(block: RequestSpecificationBuilder<String>.() -> Unit): BuildingStep<String> =
        this.head(name = null, requestType = String::class, block = block)

    /**
     * Configures an HTTP OPTIONS request specification using the provided block and returns a `BuildingStep`
     * instance for further customization. This method uses the HTTP OPTIONS method to define the request
     * specification within the provided lambda.
     *
     * @param P type of the request payload
     *  @param requestType The class type of the request body.
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the OPTIONS request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun <P : Any> options(
        name: String? = null,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = StubConfiguration(name),
            httpMethod = Options,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures and executes an HTTP OPTIONS request based on the given parameters.
     *
     * @param configuration The configuration settings to use for the request.
     * @param requestType The class type of the request body.
     * @param block A lambda to build and modify the request specifications.
     * @return A [BuildingStep] object representing the state after configuring the OPTIONS request.
     */
    public fun <P : Any> options(
        configuration: StubConfiguration,
        requestType: KClass<P>,
        block: RequestSpecificationBuilder<P>.() -> Unit,
    ): BuildingStep<P> =
        method(
            configuration = configuration,
            httpMethod = Options,
            requestType = requestType,
            block = block,
        )

    /**
     * Configures an HTTP HEAD request specification using the provided block and returns a `BuildingStep`
     * for further customization. This method serves as a convenience shortcut.
     *
     * @param block A lambda used to configure the `RequestSpecificationBuilder` for the GET request.
     * @return A `BuildingStep` instance initialized with the generated request specification.
     */
    public fun options(
        block: RequestSpecificationBuilder<String>.() -> Unit,
    ): BuildingStep<String> = this.options(name = null, requestType = String::class, block = block)

    /**
     * Retrieves a list of all request specifications that have not been matched to any incoming requests.
     * A request is considered unmatched if its match count is zero.
     *
     * @return A list of unmatched request specifications.
     */
    public fun findAllUnmatchedRequests(): List<RequestSpecification<*>> =
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
                            .toLogString()
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
