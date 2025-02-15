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
    stubs: Collection<Stub<*>>,
) {
    val request = context.call.request
    application.log.info(
        "Request: ${request.toLogString()}",
    )
    val matchedStub: Stub<*>? =
        stubs
            .filter {
                it.requestSpecification.matches(request)
            }.minWithOrNull(StubComparator)

    if (matchedStub != null) {
        matchedStub.apply {
            application.log.info(
                "Request matched: {}",
                requestSpecification.toDescription(),
            )
            incrementMatchCount()
            respond(context.call)
        }
    } else {
        application.log.warn(
            "No matched mapping for request:\n---\n${printRequest(request)}\n---",
        )
        failure("No matched mapping for request: ${printRequest(request)}")
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
