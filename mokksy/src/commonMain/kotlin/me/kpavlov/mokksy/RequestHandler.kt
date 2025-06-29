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
        matchedStub.configuration.apply {
            if (removeAfterMatch) {
                if (stubs.remove(matchedStub) && verbose) {
                    application.log.debug(
                        "Removed used stub: {}",
                        matchedStub.toLogString(),
                    )
                }
            }
        }
        handleMatchedStub(
            matchedStub = matchedStub,
            serverConfig = configuration,
            application = application,
            request = request,
            context = context,
        )
    } else {
        if (configuration.verbose) {
            application.log.warn(
                "No stubs found for request:\n---\n${printRequest(request)}\n---\nAvailable stubs:\n{}\n",
                stubs.joinToString("\n---\n") { it.toLogString() },
            )
        } else {
            application.log.warn(
                "No matched mapping for request:\n---\n${printRequest(request)}\n---",
            )
        }
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
}

private suspend fun printRequest(request: RoutingRequest): String {
    val body = request.call.receive(String::class)
    return """
        |${request.httpMethod} ${request.uri}
        |${request.headers.entries().joinToString("\n") { "${it.key}: ${it.value}" }}
        |
        |$body
        """.trimMargin()
}
