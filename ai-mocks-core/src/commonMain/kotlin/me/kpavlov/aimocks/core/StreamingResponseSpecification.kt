package me.kpavlov.aimocks.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.stream.consumeAsFlow
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import java.util.stream.Stream
import kotlin.time.Duration


public interface StreamingResponseSpecification<T : Any> {
    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param chunks The chunks of content to include in the streaming response.
     */
    public fun chunks(chunks: List<T>)

    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param chunks The chunks of content to include in the streaming response.
     * @return This specification instance for method chaining.
     */
    public fun chunks(vararg chunks: T)

    public fun stream(stream: Stream<T>)
}

/**
 * @param P The type of the request body.
 * @param T The type of the chunk element.
 * @param R The type of the response chunk.
 */
public abstract class AbstractStreamingResponseSpecification<P : Any, T : Any, R : Any>(
    response: AbstractResponseDefinition<R>,
    public var responseFlow: Flow<T>?,
    public var responseChunks: List<T>?,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<P, R>(response = response, delay = delay),
    StreamingResponseSpecification<T> {

    public override fun chunks(chunks: List<T>) {
        this.responseChunks = chunks
    }

    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param chunks The chunks of content to include in the streaming response.
     * @return This specification instance for method chaining.
     */
    public override fun chunks(vararg chunks: T) {
        this.responseChunks = chunks.toList()
    }

    public override fun stream(stream: Stream<T>) {
        responseFlow = stream.consumeAsFlow()
    }

}
