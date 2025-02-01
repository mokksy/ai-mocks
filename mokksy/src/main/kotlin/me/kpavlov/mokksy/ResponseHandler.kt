package me.kpavlov.mokksy

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.cacheControl
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondTextWriter
import io.ktor.server.sse.SSEServerContent
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Handles streaming responses for the specified `StreamResponseDefinition` and the given `ApplicationCall`.
 * This method distinguishes between streaming response types, such as Server-Sent Events (SSE)
 * and standard chunked data, to appropriately process and send the response to the client.
 *
 * @param T The type of the data being streamed in the response.
 * @param responseDefinition Defines the streaming response, including the data chunks and content type.
 * @param call The `ApplicationCall` instance associated with the current HTTP request.
 */
internal suspend fun <T> respondWithStream(
    responseDefinition: StreamResponseDefinition<T>,
    call: ApplicationCall,
) {
    val chunkFlow = responseDefinition.chunkFlow
    when {
        responseDefinition is SseStreamResponseDefinition -> {
            val chunkFlow: Flow<ServerSentEvent> = responseDefinition.chunkFlow ?: emptyFlow()
            val sseContent =
                SSEServerContent(call) {
                    chunkFlow.collect {
                        send(it)
                    }
                }
            processSSE(call, sseContent)
        }

        chunkFlow != null -> {
            call.response.cacheControl(CacheControl.NoCache(null))
            call.respondTextWriter(
                status = responseDefinition.httpStatus,
                contentType = responseDefinition.contentType,
            ) {
                responseDefinition.writeChunksFromFlow(writer = this)
            }
        }

        else -> {
            call.response.cacheControl(CacheControl.NoCache(null))
            call.respondTextWriter(
                status = responseDefinition.httpStatus,
                contentType = responseDefinition.contentType,
            ) {
                responseDefinition.writeChunksFromList(this)
            }
        }
    }
}

/**
 * Sends a Server-Sent Events (SSE) stream response for the given request.
 *
 * The method uses the provided `SseStreamResponseDefinition` to determine the stream of
 * Server-Sent Events (SSE) to send. It processes the SSE stream and sends events to the client
 * immediately as they are collected.
 *
 * @param responseDefinition Defines the SSE stream response, including the flow of server-sent events to be sent.
 * @param call The `ApplicationCall` representing the current HTTP request and response context.
 */
internal suspend fun respondWithSseStream(
    responseDefinition: SseStreamResponseDefinition,
    call: ApplicationCall,
) {
    val chunkFlow = responseDefinition.chunkFlow ?: emptyFlow()
    val sseContent =
        SSEServerContent(call) {
            chunkFlow.collect {
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
@Suppress("unused")
private suspend fun processSSE(
    call: ApplicationCall,
    content: SSEServerContent,
) {
    call.response.header(HttpHeaders.ContentType, ContentType.Text.EventStream.toString())
    call.response.header(HttpHeaders.CacheControl, "no-store")
    call.response.header(HttpHeaders.Connection, "keep-alive")
    call.response.header("X-Accel-Buffering", "no")
    call.response.status(HttpStatusCode.OK)
    call.respond(content)
}
