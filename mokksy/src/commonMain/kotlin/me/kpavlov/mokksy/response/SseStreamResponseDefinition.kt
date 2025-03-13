package me.kpavlov.mokksy.response

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.sse.SSEServerContent
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.time.Duration

public open class SseStreamResponseDefinition<P>(
    override val chunkFlow: Flow<ServerSentEvent>? = null,
    delay: Duration,
) : StreamResponseDefinition<P, ServerSentEvent>(delay = delay) {
    override suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        val theFlow = this.chunkFlow ?: emptyFlow()
        val sseContent =
            SSEServerContent(call) {
                theFlow.collect {
                    if (verbose) {
                        call.application.log.debug("Sending {}: {}", httpStatus, it)
                    }
                    send(it)
                }
            }
        processSSE(call, sseContent)
    }

    /**
     * Handles a server-sent events (SSE) response by configuring the appropriate HTTP headers
     * and sending the specified content to the client.
     *
     * @param call The [ApplicationCall] representing the current client-server interaction.
     * @param content The [SSEServerContent] that represents the server-sent events to be delivered.
     */
    private suspend fun processSSE(
        call: ApplicationCall,
        content: SSEServerContent,
    ) {
        call.response.header(HttpHeaders.CacheControl, "no-store")
        call.response.header(HttpHeaders.Connection, "keep-alive")
        call.response.header("X-Accel-Buffering", "no")
        call.response.status(HttpStatusCode.OK)
        call.respond(content)
    }
}
