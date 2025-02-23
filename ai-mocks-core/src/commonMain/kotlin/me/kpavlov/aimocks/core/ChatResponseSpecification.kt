package me.kpavlov.aimocks.core

import me.kpavlov.mokksy.response.AbstractResponseDefinition

public open class ChatResponseSpecification<P : Any, T : Any>(
    protected val response: AbstractResponseDefinition<P, T>,
)
