package dev.mokksy.aimocks.core

import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

public interface ResponseSpecification {
    /**
     * Sets the delay before sending the response, in milliseconds.
     *
     * @param value The delay duration in milliseconds.
     * @return This specification instance for method chaining.
     */
    public fun delayMillis(value: Long)
}

/**
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @param delay delay to first token
 */
public abstract class AbstractResponseSpecification<P : Any, T : Any>(
    protected val response: AbstractResponseDefinition<T>,
    public var delay: Duration,
) : ResponseSpecification {
    /**
     * Sets the delay before sending the response, in milliseconds.
     *
     * @param value The delay duration in milliseconds.
     * @return This specification instance for method chaining.
     */
    public override fun delayMillis(value: Long) {
        this.delay = value.milliseconds
    }
}
