package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecification
import dev.mokksy.mokksy.request.RequestSpecificationBuilder
import dev.mokksy.mokksy.request.methodEqual
import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.kotest.assertions.failure
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
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

private const val DEFAULT_HOST = "127.0.0.1"

/**
 * Creates and returns an embedded Ktor server instance
 * with the specified host, port, server configuration, and application module.
 *
 * This function is platform-specific and must be implemented for each supported target.
 *
 * @param host The host name on which the server will run. Default value is [DEFAULT_HOST] (`127.0.0.1`).
 * @param port The port number on which the server will run. Default value is `0` - randomly assigned port.
 * @param configuration The [ServerConfiguration] settings.
 * @param module The application module to install in the server.
 * @return An embedded server instance configured with the provided parameters.
 * @author Konstantin Pavlov
 */
internal expect fun createEmbeddedServer(
    host: String = DEFAULT_HOST,
    port: Int = 0,
    configuration: ServerConfiguration,
    module: Application.() -> Unit,
): EmbeddedServer<
    out ApplicationEngine,
    out ApplicationEngine.Configuration,
>

/**
 * Configures content negotiation for the server using the provided configuration.
 *
 * Platform-specific implementations should install and set up content negotiation plugins as needed.
 *
 * @param config The content negotiation configuration to apply.
 */
internal expect fun configureContentNegotiation(config: ContentNegotiationConfig)

public typealias ApplicationConfigurer = (Application.() -> Unit)

/**
 * Represents an embedded mock server capable of handling various HTTP requests and responses for testing purposes.
 * Provides functionality to configure request specifications for different HTTP methods and manage request matching.
 *
 * @constructor Initializes the server with the specified parameters and starts it.
 * @param host The host name on which the server will run. Default value is [DEFAULT_HOST] (`127.0.0.1`).
 * @param port The port number on which the server will run. Default value is `0` - randomly assigned port.
 * @param configuration [ServerConfiguration] options
 * @param wait Determines whether the server startup process should block the current thread. Defaults to false.
 * @param configurer A lambda function for setting custom configurations for the server's application module.
 * @author Konstantin Pavlov
 */
@Suppress("TooManyFunctions")
public open class MokksyServer
    @JvmOverloads
    constructor(
        host: String = DEFAULT_HOST,
        port: Int = 0,
        configuration: ServerConfiguration,
        wait: Boolean = false,
        configurer: ApplicationConfigurer = {},
    ) {
        /**
         *  @constructor Initializes the server with the specified parameters and starts it.
         *  @param port The port number on which the server will run. Defaults to 0 (randomly assigned port).
         *  @param verbose A flag indicating whether detailed logs should be printed. Defaults to false.
         *  @param wait Determines whether the server startup process should block the current thread.
         *  Defaults to false.
         *  @param configurer A lambda function for setting custom configurations for the server's application module.
         */
        @JvmOverloads
        public constructor(
            port: Int = 0,
            host: String = DEFAULT_HOST,
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

        public lateinit var logger: io.ktor.util.logging.Logger
        protected val httpFormatter: HttpFormatter = HttpFormatter()

        private val stubRegistry = StubRegistry()

        private val server =
            createEmbeddedServer(
                host = host,
                port = port,
                configuration = configuration,
            ) {
                logger = this.environment.log

                install(SSE)
                install(DoubleReceive)
                install(ContentNegotiation) {
                    configuration.contentNegotiationConfigurer(this)
                }

                routing {
                    route("{...}") {
                        handle {
                            handleRequest(
                                context = this@handle,
                                application = this@createEmbeddedServer,
                                stubRegistry = stubRegistry,
                                configuration = configuration,
                                formatter = httpFormatter,
                            )
                        }
                    }
                }
                configurer(this)
            }

        init {
            server.start(wait = wait)
            runBlocking {
                resolvedPort =
                    server.engine
                        .resolvedConnectors()
                        .single()
                        .port
            }
        }

        /**
         * Adds a [Stub] to the server's collection, asserting that it is not a duplicate.
         *
         * @param stub The [Stub] to register.
         * @throws AssertionError if the stub is already registered.
         */
        private fun registerStub(stub: Stub<*, *>) {
            stubRegistry.add(stub)
        }

        /**
         * Returns the actual port number the server is bound to after startup.
         *
         * @return The resolved server port.
         */
        public fun port(): Int = resolvedPort

        public fun baseUrl(): String = "http://127.0.0.1:$resolvedPort"

        /**
         * Creates a [RequestSpecification] for the given HTTP method and request type,
         * and returns a [BuildingStep] for further stub configuration.
         *
         * @param configuration The [StubConfiguration] to use for this request specification.
         * @param httpMethod The HTTP method to match for incoming requests.
         * @param requestType The class type of the expected request body.
         * @param block Lambda to configure the [RequestSpecificationBuilder].
         * @return A [BuildingStep] for further customization and stub registration.
         */
        public fun <P : Any> method(
            configuration: StubConfiguration,
            httpMethod: HttpMethod,
            requestType: KClass<P>,
            block: RequestSpecificationBuilder<P>.() -> Unit,
        ): BuildingStep<P> {
            val requestSpec =
                RequestSpecificationBuilder(requestType)
                    .apply(block)
                    .method(methodEqual(httpMethod))
                    .build()

            return BuildingStep(
                configuration = configuration,
                requestSpecification = requestSpec,
                registerStub = this::registerStub,
                requestType = requestType,
                formatter = httpFormatter,
            )
        }

        /**
         * Defines a stubbed HTTP [RequestSpecification] for the given method and request type,
         * optionally naming the stub.
         *
         * @param name Optional identifier for the stub.
         * @param httpMethod The HTTP method to match (e.g., GET, POST).
         * @param requestType The expected type of the request payload.
         * @param block Lambda to configure the [RequestSpecificationBuilder].
         * @return A [BuildingStep] for further stub configuration and registration.
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
         * Registers a stub for an HTTP GET request with the specified configuration and request type.
         *
         * @param requestType The class representing the expected request body type.
         * @param block Lambda to configure the [RequestSpecificationBuilder] for the GET request.
         * @return A [BuildingStep] for further customization and response definition.
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
         * Defines a stub for an HTTP GET request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub, used for identification.
         * @param requestType The class of the expected request body.
         * @param block Lambda to configure the [RequestSpecificationBuilder].
         * @return A [BuildingStep] for further stub customization.
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
         * Defines a stub for an HTTP GET request with a string body using the provided configuration block.
         *
         * Returns a `BuildingStep` for further customization of the stubbed GET request.
         */
        public fun get(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> =
            this.get(
                name = null,
                requestType = String::class,
                block = block,
            )

        /**
         * Defines a stub for an HTTP GET request with the specified configuration and request specification builder.
         *
         * @param configuration The stub configuration for this GET request.
         * @param block Lambda to configure the request specification builder.
         * @return A [BuildingStep] for further customization of the stub.
         */
        public fun get(
            configuration: StubConfiguration,
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> =
            this.get(
                configuration = configuration,
                requestType = String::class,
                block = block,
            )

        /**
         * Defines a stub for an HTTP POST request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub.
         * @param requestType The class of the request body to match.
         * @param block Lambda to configure the request specification.
         * @return A `BuildingStep` for further stub customization.
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
         * Defines a POST request stub with the specified configuration and request type.
         *
         * @param configuration Stub configuration specifying endpoint and matching criteria.
         * @param requestType The class of the expected request body.
         * @param block Lambda to configure the request specification details.
         * @return A [BuildingStep] for further stub setup or response definition.
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
         * Defines a stub for an HTTP POST request with a string request body.
         *
         * @param block Lambda to configure the request specification builder.
         * @return A building step for further customization of the POST request stub.
         */
        public fun post(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> = this.post(name = null, requestType = String::class, block = block)

        /**
         * Defines a stub for an HTTP DELETE request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub.
         * @param requestType The class of the request body to match.
         * @param block Lambda to configure the request specification.
         * @return A `BuildingStep` for further stub customization.
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
         * Registers a stub for an HTTP DELETE request with the specified configuration and request type.
         *
         * @return A BuildingStep for further configuration or response definition of the DELETE request stub.
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
         * Defines a stub for an HTTP DELETE request with a string request body.
         *
         * @param block Lambda to configure the request specification builder.
         * @return A building step for further stub customization.
         */
        public fun delete(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> =
            this.delete(name = null, requestType = String::class, block = block)

        /**
         * Defines a stub for an HTTP PATCH request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub.
         * @param requestType The class of the request payload.
         * @param block Lambda to configure the request specification.
         * @return A building step for further stub customization.
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
         * Creates a stub for a PATCH HTTP request with the specified configuration, request type,
         * and request specification.
         *
         * @return A [BuildingStep] for further configuring the PATCH request stub.
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
         * Defines a PATCH request stub with a string request body using the provided configuration block.
         *
         * @param block Lambda to configure the request specification builder for the PATCH request.
         * @return A `BuildingStep` for further customization of the PATCH request stub.
         */
        public fun patch(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> =
            this.patch(name = null, requestType = String::class, block = block)

        /**
         * Defines a stub for an HTTP PUT request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub.
         * @param requestType The class of the request payload.
         * @param block Lambda to configure the request specification.
         * @return A `BuildingStep` for further customization of the stub.
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
         * Defines a stub for an HTTP PUT request with the specified configuration and request type.
         *
         * @param configuration The stub configuration for this request.
         * @param requestType The class representing the request payload type.
         * @param block Lambda to configure the request specification.
         * @return A [BuildingStep] for further stub setup.
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
         * Defines a stub for an HTTP PUT request with a string request body.
         *
         * @param block Lambda to configure the request specification builder.
         * @return A building step for further stub customization.
         */
        public fun put(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> = this.put(name = null, requestType = String::class, block = block)

        /**
         * Defines a stub for an HTTP HEAD request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub.
         * @param requestType The class of the request body.
         * @param block Lambda to configure the request specification.
         * @return A `BuildingStep` for further customization of the stub.
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
         * Defines a stub for a HEAD HTTP request with the specified configuration and request type.
         *
         * @param configuration The stub configuration for this request.
         * @param requestType The class representing the request payload type.
         * @param block Lambda to configure the request specification.
         * @return A [BuildingStep] for further stub setup.
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
         * Defines a stub for an HTTP HEAD request with a string request body.
         *
         * @param block Lambda to configure the request specification builder.
         * @return A building step for further customization of the HEAD request stub.
         */
        public fun head(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> = this.head(name = null, requestType = String::class, block = block)

        /**
         * Defines a stub for an HTTP OPTIONS request with the specified request type and configuration block.
         *
         * @param name Optional name for the stub.
         * @param requestType The class of the request payload.
         * @param block Lambda to configure the request specification.
         * @return A `BuildingStep` for further customization of the stub.
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
         * Defines a stub for an HTTP OPTIONS request with the specified configuration and request type.
         *
         * @param configuration Stub configuration settings for this request.
         * @param requestType The class representing the expected request body type.
         * @param block Lambda to configure the request specification.
         * @return A [BuildingStep] for further stub setup or response definition.
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
         * Defines an HTTP OPTIONS request stub with a string request body using the provided configuration block.
         *
         * @param block Lambda to configure the request specification builder for the OPTIONS request.
         * @return A `BuildingStep` for further customization of the stub.
         */
        public fun options(
            block: RequestSpecificationBuilder<String>.() -> Unit,
        ): BuildingStep<String> =
            this.options(name = null, requestType = String::class, block = block)

        /**
         * Returns all request specifications that have not matched any incoming requests.
         *
         * A request specification is considered unmatched if its associated stub's match count is zero.
         *
         * @return A list of unmatched request specifications.
         */
        public fun findAllUnmatchedRequests(): List<RequestSpecification<*>> =
            stubRegistry
                .getAll()
                .filter {
                    it.matchCount() == 0
                }.map { it.requestSpecification }
                .toList()

        /**
         * Resets the match count of all registered stubs to zero.
         *
         * Use this to clear match history before running new tests or scenarios.
         */
        public fun resetMatchCounts() {
            stubRegistry
                .getAll()
                .forEach {
                    it.resetMatchCount()
                }
        }

        /**
         * Verifies that all registered request stubs have been matched at least once.
         *
         * Throws an error if any request specification was not triggered during execution,
         * listing all unmatched requests.
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
         * Stops the embedded server and releases its resources
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
            require(gracePeriodMillis >= 0) { "gracePeriodMillis must be >= 0" }
            require(timeoutMillis >= 0) { "timeoutMillis must be >= 0" }
            require(
                timeoutMillis >= gracePeriodMillis,
            ) { "timeoutMillis must be >= gracePeriodMillis" }

            server.stop(gracePeriodMillis, timeoutMillis, TimeUnit.MILLISECONDS)
        }
    }

/**
 * A typealias for MokksyServer, allowing the use of `Mokksy`
 * as an alternative, more concise name for referencing the `MokksyServer` class.
 */
public typealias Mokksy = MokksyServer
