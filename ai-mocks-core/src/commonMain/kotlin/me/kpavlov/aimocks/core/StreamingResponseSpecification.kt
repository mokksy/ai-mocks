package me.kpavlov.aimocks.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.stream.consumeAsFlow
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import java.util.stream.Stream
import kotlin.time.Duration

/**
 * @param P The type of the request body.
 * @param T The type of the chunk element.
 * @param R The type of the response chunk.
 */
public abstract class StreamingResponseSpecification<P : Any, T : Any, R : Any>(
    response: AbstractResponseDefinition<R>,
    public var responseFlow: Flow<T>?,
    public var responseChunks: List<T>?,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
) : ResponseSpecification<P,R>(response = response, delay=delay) {

    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param chunks The chunks of content to include in the streaming response.
     */
    public open fun chunks(chunks: List<T>) {
        this.responseChunks = chunks
    }

    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param vararg chunks The chunks of content to include in the streaming response.
     * @return This specification instance for method chaining.
     */
    public open fun chunks(vararg chunks: T) {
        this.responseChunks = chunks.toList()
    }

    public fun stream(stream: Stream<T>) {
        responseFlow = stream.consumeAsFlow()
    }

}
