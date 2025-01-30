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
