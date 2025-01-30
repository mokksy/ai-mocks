package me.kpavlov.mokksy

import io.kotest.assertions.failure
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingContext

/**
 * Handles an incoming HTTP request by identifying the appropriate mapping based on the request
 * parameters, then processes and sends the response accordingly. If no mapping is matched,
 * logs a failure.
 *
 * @param context The routing context containing the request and response handlers.
 * @param application The Ktor application instance used for logging and other application-level operations.
 * @param mappings A collection of mappings that specify how incoming requests should be processed and responded to.
 */
internal suspend fun handleRequest(
    context: RoutingContext,
    application: Application,
    mappings: Collection<Mapping<*>>,
) {
    val request = context.call.request
    application.log.info(
        "Request: ${request.httpMethod.value.uppercase()} ${request.uri}",
    )
    val matchedMapping: Mapping<*>? =
        mappings
            .filter {
                it.requestSpecification.matches(request)
            }.minByOrNull { it.requestSpecification.priority }

    if (matchedMapping != null) {
        matchedMapping.apply {
            application.log.info(
                "Request matched: {}",
                requestSpecification.toDescription(),
            )
            incrementMatchCount()
            this.respond(context.call)
        }
    } else {
        failure("No matched mapping for request: $request")
    }
}
