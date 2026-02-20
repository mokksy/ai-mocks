package dev.mokksy.mokksy.request

import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingRequest

/**
 * Immutable snapshot of an HTTP request for recording purposes.
 *
 * Unlike [CapturedRequest], this class does not retain references to
 * the underlying request or connection, making it safe for long-term storage
 * (e.g., tracking requests that arrived with no matching stub).
 *
 * @property method The HTTP method of the request.
 * @property uri The URI of the request.
 * @property headers The headers of the request.
 */
public data class RecordedRequest(
    val method: HttpMethod,
    val uri: String,
    val headers: Map<String, List<String>>,
) {
    public companion object {
        /**
         * Creates a [RecordedRequest] snapshot from a [io.ktor.server.routing.RoutingRequest].
         */
        public fun from(request: RoutingRequest): RecordedRequest =
            RecordedRequest(
                method = request.local.method,
                uri = request.local.uri,
                headers = request.headers.entries().associate { it.key to it.value },
            )
    }

    override fun toString(): String = "${method.value} $uri"
}
