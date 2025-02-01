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
 * @param stubs A collection of mappings that specify how incoming requests should be processed and responded to.
 */
internal suspend fun handleRequest(
    context: RoutingContext,
    application: Application,
    stubs: Collection<Stub<*>>,
) {
    val request = context.call.request
    application.log.info(
        "Request: ${request.httpMethod.value.uppercase()} ${request.uri}",
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
            this.respond(context.call)
        }
    } else {
        failure("No matched mapping for request: $request")
    }
}
