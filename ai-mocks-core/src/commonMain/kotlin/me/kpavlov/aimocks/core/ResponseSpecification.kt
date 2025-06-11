package me.kpavlov.aimocks.core

import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @param delay delay to first token
 */
public abstract class ResponseSpecification<P : Any, T : Any>(
    protected val response: AbstractResponseDefinition<T>,
    public var delay: Duration,
) {

    /**
     * Sets the delay before sending the response (time to first token).
     *
     * @param delayMs The delay in milliseconds.
     * @return This specification instance for method chaining.
     */
    public fun delayMillis(value: Long) {
        this.delay = value.milliseconds
    }
}
