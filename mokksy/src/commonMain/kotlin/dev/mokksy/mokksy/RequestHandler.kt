package dev.mokksy.mokksy

import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.kotest.assertions.failure
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.logging.toLogString
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingRequest

/**
 * Processes an incoming HTTP request by matching it against available stubs and handling the response.
 *
 * Attempts to find the best matching stub for the request.
 * If a match is found, processes the stub and optionally removes it based on configuration.
 * If no match is found, it logs the event and triggers a failure.
 *
 * @param context The routing context containing the request and response.
 * @param stubRegistry The registry of available stubs to match against.
 * @param configuration Server configuration settings that influence matching and logging behavior.
 * @param formatter Formats HTTP requests for logging and error messages.
 * @author Konstantin Pavlov
 */
internal suspend fun handleRequest(
    context: RoutingContext,
    application: Application,
    stubRegistry: StubRegistry,
    configuration: ServerConfiguration,
    formatter: HttpFormatter,
) {
    val request = context.call.request

    val matchedStub: Stub<*, *>? =
        stubRegistry.findMatchingStub(
            request = request,
            verbose = configuration.verbose,
            logger = application.log,
            formatter = formatter,
        )

    if (matchedStub != null) {
        handleMatchedStub(
            matchedStub = matchedStub,
            serverConfig = configuration,
            application = application,
            request = request,
            context = context,
            formatter = formatter,
        )
    } else {
        if (configuration.verbose) {
            application.log.warn(
                "NO STUBS FOUND for the request:\n---\n${
                    formatter.formatRequest(request)
                }\n---\nAvailable stubs:\n{}\n",
                stubRegistry.getAll().joinToString("\n---\n") { it.toLogString() },
            )
        } else {
            application.log.warn(
                "No matched mapping for request:\n---\n${request.toLogString()}\n---",
            )
        }
        failure("No matched mapping for request: ${request.toLogString()}")
    }
}

/**
 * Processes a matched stub by logging the match, incrementing its match count,
 * and sending the stubbed response.
 *
 * If verbose logging is enabled in either the server or stub configuration,
 * logs detailed information about the matched request and stub.
 */
@Suppress("LongParameterList")
private suspend fun handleMatchedStub(
    matchedStub: Stub<*, *>,
    serverConfig: ServerConfiguration,
    application: Application,
    request: RoutingRequest,
    context: RoutingContext,
    formatter: HttpFormatter,
) {
    val config = matchedStub.configuration
    val verbose = serverConfig.verbose || config.verbose

    matchedStub.apply {
        if (verbose) {
            application.log.info(
                "Request matched:\n---\n${formatter.formatRequest(request)}---\n{}",
                this.toLogString(),
            )
        }
        incrementMatchCount()
        respond(context.call, verbose)
    }
}
