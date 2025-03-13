package me.kpavlov.mokksy

import io.kotest.assertions.failure
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.logging.toLogString
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingRequest

/**
 * Handles an incoming HTTP request by identifying the appropriate mapping based on the request
 * parameters, then processes and sends the response accordingly. If no mapping is matched,
 * logs a failure.
 *
 * @param context The routing context containing the request and response handlers.
 * @param application The Ktor application instance used for logging and other application-level operations.
 * @param stubs A collection of mappings that specify how incoming requests should be processed and responded to.
 */
internal suspend fun handleRequest(
    context: RoutingContext,
    application: Application,
    stubs: MutableSet<Stub<*, *>>,
    configuration: ServerConfiguration,
) {
    val request = context.call.request
    application.log.info(
        "Request: ${request.toLogString()}",
    )
    val matchedStub: Stub<*, *>? =
        stubs
            .filter { stub ->
                val result =
                    stub.requestSpecification
                        .matches(request)
                        .onFailure {
                            if (configuration.verbose) {
                                application.log.warn(
                                    "Failed to evaluate condition for stub:\n---\n{}\n---" +
                                        "\nand request:\n---\n{}\n---",
                                    stub.toLogString(),
                                    printRequest(request),
                                    it,
                                )
                            }
                        }
                result.getOrNull() == true
            }.minWithOrNull(StubComparator)

    if (matchedStub != null) {
        handleMatchedStub(
            matchedStub = matchedStub,
            serverConfig = configuration,
            application = application,
            request = request,
            context = context,
            stubs = stubs,
        )
    } else {
        application.log.warn(
            "No matched mapping for request:\n---\n${printRequest(request)}\n---",
        )
        failure("No matched mapping for request: ${printRequest(request)}")
    }
}

@Suppress("LongParameterList")
private suspend fun handleMatchedStub(
    matchedStub: Stub<*, *>,
    serverConfig: ServerConfiguration,
    application: Application,
    request: RoutingRequest,
    context: RoutingContext,
    stubs: MutableSet<Stub<*, *>>,
) {
    val config = matchedStub.configuration
    val verbose = serverConfig.verbose || config.verbose

    matchedStub.apply {
        if (verbose) {
            application.log.info(
                "Request matched:\n---\n${printRequest(request)}\n---\nStub: {}",
                this.toLogString(),
            )
        }
        incrementMatchCount()
        respond(context.call, verbose)
    }

    if (config.removeAfterMatch) {
        if (verbose) {
            application.log.info(
                "Removing used stub: {}",
                matchedStub.toLogString(),
            )
        }
        stubs.remove(matchedStub)
    }
}

private suspend fun printRequest(request: RoutingRequest): String {
    val body = request.call.receive(String::class)
    return """
        |${request.httpMethod} ${request.uri}
        |${request.headers.entries().joinToString("\n") { "${it.key}: ${it.value}" }}
        |$body
        """.trimMargin()
}
