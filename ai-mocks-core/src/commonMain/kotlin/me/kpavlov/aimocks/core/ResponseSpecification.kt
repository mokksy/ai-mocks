package me.kpavlov.aimocks.core

import me.kpavlov.mokksy.response.AbstractResponseDefinition

/**
 * @param P The type of the request body.
 * @param T The type of the response body.
 */
public open class ResponseSpecification<P : Any, T : Any>(
    protected val response: AbstractResponseDefinition<T>,
)
